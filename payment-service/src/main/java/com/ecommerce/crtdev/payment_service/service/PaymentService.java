package com.ecommerce.crtdev.payment_service.service;

import com.ecommerce.crtdev.payment_service.client.SellerClient;
import com.ecommerce.crtdev.payment_service.dto.BankInfoResponse;
import com.ecommerce.crtdev.payment_service.dto.PaymentRequests.TokenizeCardRequest;
import com.ecommerce.crtdev.payment_service.dto.PaymentResponses.*;
import com.ecommerce.crtdev.payment_service.entity.*;
import com.ecommerce.crtdev.payment_service.exception.PaymentNotFoundException;
import com.ecommerce.crtdev.payment_service.kafka.event.order.OrderReadyForPaymentEvent;
import com.ecommerce.crtdev.payment_service.kafka.event.order.PaymentRefundRequestEvent;
import com.ecommerce.crtdev.payment_service.kafka.producer.PaymentEventPublisher;
import com.ecommerce.crtdev.payment_service.repository.*;
import com.fasterxml.uuid.Generators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private static final double SUCCESS_RATE = 0.90;

    private final PaymentMethodRepository  paymentMethodRepository;
    private final PaymentRepository        paymentRepository;
    private final PaymentRefundRepository  paymentRefundRepository;
    private final SellerClient             sellerClient;
    private final PaymentEventPublisher eventPublisher;
    private final Random                   random = new Random();

    public PaymentService(PaymentMethodRepository paymentMethodRepository,
                          PaymentRepository paymentRepository,
                          PaymentRefundRepository paymentRefundRepository,
                          SellerClient sellerClient,
                          PaymentEventPublisher eventPublisher) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository       = paymentRepository;
        this.paymentRefundRepository = paymentRefundRepository;
        this.sellerClient            = sellerClient;
        this.eventPublisher          = eventPublisher;
    }

    @Transactional
    public TokenizeResponse tokenize(Long userId, TokenizeCardRequest request) {
        String cardNumber = request.cardNumber().replaceAll("\\s", "");

        String last4 = cardNumber.substring(cardNumber.length() - 4);
        CardBrand brand = CardBrand.detect(cardNumber);

        String token = Generators.timeBasedEpochGenerator().generate().toString();

        PaymentMethod paymentMethod = new PaymentMethod(
                token, userId, last4, brand,
                request.holderName(), request.expiryMonth(), request.expiryYear()
        );

        paymentMethodRepository.save(paymentMethod);

        log.info("Card tokenized for userId={} brand={} last4={}", userId, brand, last4);
        return TokenizeResponse.from(paymentMethod);
    }

    @Transactional
    public void processPayment(OrderReadyForPaymentEvent event,
                               UUID incomingEventId, UUID correlationId,
                               UUID checkoutId) {

        if (paymentRepository.findByOrderId(event.orderId()).isPresent()) {
            log.warn("Payment already processed for orderId={}", event.orderId());
            return;
        }

        BankInfoResponse bankInfo = sellerClient.getBankInfo(event.sellerId());

        String paymentId = Generators.timeBasedEpochGenerator().generate().toString();
        Payment payment = new Payment(
                paymentId, event.orderId(), event.userId(),
                event.sellerId(), event.paymentMethodToken(), event.totalAmount()
        );

        boolean success = random.nextDouble() < SUCCESS_RATE;

        if (success) {
            payment.markSuccess();
            paymentRepository.save(payment);

            log.info("Payment SUCCESS orderId={} amount={} seller={}",
                    event.orderId(), event.totalAmount(), bankInfo.bankName());

            eventPublisher.publishPaymentSuccess(
                    event.orderId(), correlationId, checkoutId, incomingEventId);
        } else {
            String reason = "Payment declined by issuer";
            payment.markFailed(reason);
            paymentRepository.save(payment);

            log.warn("Payment FAILED orderId={} reason={}", event.orderId(), reason);

            eventPublisher.publishPaymentFailed(
                    event.orderId(), reason, correlationId, checkoutId, incomingEventId);
        }
    }

    @Transactional
    public void processRefund(PaymentRefundRequestEvent event,
                              UUID incomingEventId, UUID correlationId,
                              UUID checkoutId) {

        if (paymentRefundRepository.findByOrderId(event.orderId()).isPresent()) {
            log.warn("Refund already processed for orderId={}", event.orderId());
            return;
        }

        Payment payment = paymentRepository.findByOrderId(event.orderId())
                .orElseThrow(() -> new PaymentNotFoundException(event.orderId()));

        String refundId = Generators.timeBasedEpochGenerator().generate().toString();
        PaymentRefund refund = new PaymentRefund(
                refundId, payment.getId(), event.orderId(), event.amount());

        refund.markCompleted();
        payment.markRefunded();

        paymentRefundRepository.save(refund);
        paymentRepository.save(payment);

        log.info("Refund COMPLETED orderId={} amount={}", event.orderId(), event.amount());

        eventPublisher.publishPaymentRefunded(
                event.orderId(), correlationId, checkoutId, incomingEventId);
    }
}
