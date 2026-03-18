package com.ecommerce.crtdev.order_service.service;

import com.ecommerce.crtdev.order_service.client.InventoryClient;
import com.ecommerce.crtdev.order_service.dto.InventoryDtos;
import com.ecommerce.crtdev.order_service.dto.OrderRequests;
import com.ecommerce.crtdev.order_service.dto.OrderResponses;
import com.ecommerce.crtdev.order_service.entity.Order;
import com.ecommerce.crtdev.order_service.entity.OrderItem;
import com.ecommerce.crtdev.order_service.entity.ProductSnapshot;
import com.ecommerce.crtdev.order_service.exception.exceptions.*;
import com.ecommerce.crtdev.order_service.kafka.publisher.OrderEventPublisher;
import com.ecommerce.crtdev.order_service.repository.OrderRepository;
import com.ecommerce.crtdev.order_service.repository.ProductSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final BigDecimal PRICE_TOLERANCE = new BigDecimal("0.01");

    private final OrderRepository orderRepository;
    private final ProductSnapshotRepository snapshotRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ProductSnapshotRepository snapshotRepository,
                        InventoryClient inventoryClient,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository    = orderRepository;
        this.snapshotRepository = snapshotRepository;
        this.inventoryClient    = inventoryClient;
        this.eventPublisher     = eventPublisher;
    }

    public OrderResponses.CheckoutResponse checkout(Long userId, String buyerEmail, OrderRequests.CheckoutRequest request) {

        UUID checkoutId = UUID.randomUUID();

        Map<Long, List<OrderItem>> itemsBySeller = groupItemsBySeller(request.items());

        List<OrderResponses.OrderSummaryResponse> created = new ArrayList<>();
        List<OrderResponses.FailedOrderResponse>  failed  = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItem>> entry : itemsBySeller.entrySet()) {
            Long            sellerId = entry.getKey();
            List<OrderItem> items    = entry.getValue();

            try {
                OrderResponses.OrderSummaryResponse order = createSellerOrder(
                        userId, buyerEmail, sellerId, items,
                        request.paymentMethodToken(), checkoutId);
                created.add(order);
            } catch (InsufficientStockException | ProductNotFoundException e) {
                log.warn("Order failed for seller {}: {}", sellerId, e.getMessage());
                failed.add(new OrderResponses.FailedOrderResponse(sellerId, e.getMessage()));
            } catch (Exception e) {
                log.error("Unexpected error for seller {}", sellerId, e);
                failed.add(new OrderResponses.FailedOrderResponse(sellerId, "Unexpected error, please retry"));
            }
        }

        return new OrderResponses.CheckoutResponse(checkoutId, created, failed);
    }

    @Transactional
    protected OrderResponses.OrderSummaryResponse createSellerOrder(
            Long userId, String buyerEmail, Long sellerId,
            List<OrderItem> items, String paymentMethodToken, UUID checkoutId) {

        List<InventoryDtos.StockItem> stockItems = items.stream()
                .map(i -> new InventoryDtos.StockItem(i.getProductId(), i.getQuantity()))
                .toList();
        inventoryClient.reserve(stockItems);

        Order order = new Order(userId, buyerEmail, sellerId,
                paymentMethodToken, checkoutId, items);
        order.markAwaitingPayment();
        orderRepository.save(order);

        eventPublisher.publishOrderReadyForPayment(order, checkoutId);

        log.info("Order {} created checkoutId={} sellerId={}", order.getId(), checkoutId, sellerId);
        return OrderResponses.OrderSummaryResponse.from(order);
    }

    @Transactional
    public void handlePaymentSuccess(UUID orderId, UUID incomingEventId) {
        Order order = findById(orderId);
        order.markPaymentProcessing();
        order.markConfirmed();
        orderRepository.save(order);

        eventPublisher.publishOrderConfirmed(
                order, order.getCheckoutId(), incomingEventId);

        log.info("Order {} confirmed correlationId={} causationId={}",
                orderId, orderId, incomingEventId);
    }

    @Transactional
    public void handlePaymentFailed(UUID orderId, String reason, UUID incomingEventId) {
        Order order = findById(orderId);
        order.markCancelled();
        orderRepository.save(order);

        eventPublisher.publishReverseStock(
                order, order.getCheckoutId(), incomingEventId);
        eventPublisher.publishOrderCancelled(
                order, order.getCheckoutId(), incomingEventId, reason);

        log.info("Order {} cancelled due to payment failure causationId={}",
                orderId, incomingEventId);
    }

    @Transactional
    public void cancelOrder(UUID orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));

        if (!order.isCancellable()) {
            throw new OrderNotCancellableException(orderId.toString(), order.getStatus());
        }

        order.markCancelled();
        orderRepository.save(order);

        eventPublisher.publishReverseStock(order, order.getCheckoutId(), null);
        eventPublisher.publishOrderCancelled(
                order, order.getCheckoutId(), null, "Cancelled by user");

        log.info("Order {} cancelled by user {}", orderId, userId);
    }

    @Transactional
    public void requestRefund(UUID orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));

        if (!order.isRefundable()) {
            throw new OrderNotRefundableException(orderId.toString(), order.getStatus());
        }

        order.markRefunding();
        orderRepository.save(order);

        eventPublisher.publishPaymentRefundRequest(order, order.getCheckoutId());

        log.info("Refund requested for order {} by user {}", orderId, userId);
    }

    @Transactional
    public void handlePaymentRefunded(UUID orderId, UUID incomingEventId) {
        Order order = findById(orderId);
        order.markRefunded();
        orderRepository.save(order);

        eventPublisher.publishReverseStock(
                order, order.getCheckoutId(), incomingEventId);
        eventPublisher.publishOrderCancelled(
                order, order.getCheckoutId(), incomingEventId, "Refunded");

        log.info("Order {} refunded causationId={}", orderId, incomingEventId);
    }

    @Transactional(readOnly = true)
    public List<OrderResponses.OrderSummaryResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(OrderResponses.OrderSummaryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponses.OrderSummaryResponse getOrder(UUID orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(OrderResponses.OrderSummaryResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
    }

    @Transactional
    public void upsertSnapshot(String productId, Long sellerId,
                               String name, BigDecimal price) {
        ProductSnapshot snapshot = snapshotRepository.findById(productId)
                .orElse(new ProductSnapshot(productId, sellerId, name, price));
        snapshotRepository.save(snapshot);
    }

    @Transactional
    public void updateSnapshotPrice(String productId, BigDecimal newPrice) {
        snapshotRepository.findById(productId).ifPresent(s -> {
            s.updatePrice(newPrice);
            snapshotRepository.save(s);
        });
    }

    @Transactional
    public void deactivateSnapshot(String productId) {
        snapshotRepository.findById(productId).ifPresent(s -> {
            s.deactivate();
            snapshotRepository.save(s);
        });
    }

    private Map<Long, List<OrderItem>> groupItemsBySeller(
            List<OrderRequests.CheckoutItemRequest> requests) {

        Map<Long, List<OrderItem>> result = new LinkedHashMap<>();

        for (OrderRequests.CheckoutItemRequest req : requests) {
            ProductSnapshot snapshot = snapshotRepository
                    .findByProductIdAndActiveTrue(req.productId())
                    .orElseThrow(() -> new ProductUnavailableException(req.productId()));

            validatePrice(req, snapshot);

            OrderItem item = new OrderItem(
                    req.productId(), snapshot.getProductName(),
                    snapshot.getPrice(), req.quantity());

            result.computeIfAbsent(snapshot.getSellerId(), k -> new ArrayList<>()).add(item);
        }

        return result;
    }

    private void validatePrice(OrderRequests.CheckoutItemRequest req, ProductSnapshot snapshot) {
        BigDecimal expected  = snapshot.getPrice();
        BigDecimal received  = req.price();
        BigDecimal tolerance = expected.multiply(PRICE_TOLERANCE);

        if (received.subtract(expected).abs().compareTo(tolerance) > 0) {
            log.warn("Price mismatch product {}: expected={} received={}",
                    req.productId(), expected, received);
            throw new PriceManipulationException(req.productId(), expected, received);
        }
    }

    private Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
    }
}
