package com.ecommerce.crtdev.seller_service.service;

import com.ecommerce.crtdev.seller_service.dto.SellerRequests;
import com.ecommerce.crtdev.seller_service.dto.SellerResponses;
import com.ecommerce.crtdev.seller_service.entity.BankAccount;
import com.ecommerce.crtdev.seller_service.entity.Seller;
import com.ecommerce.crtdev.seller_service.exception.custom.BankAccountNotFoundException;
import com.ecommerce.crtdev.seller_service.exception.custom.SellerAlreadyExistsException;
import com.ecommerce.crtdev.seller_service.exception.custom.SellerNotFoundException;
import com.ecommerce.crtdev.seller_service.kafka.producer.SellerEventPublisher;
import com.ecommerce.crtdev.seller_service.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

    private final SellerRepository     sellerRepository;
    private final SellerEventPublisher eventPublisher;

    public SellerService(SellerRepository sellerRepository,
                         SellerEventPublisher eventPublisher) {
        this.sellerRepository = sellerRepository;
        this.eventPublisher   = eventPublisher;
    }

    @Transactional
    public SellerResponses.SellerProfileResponse register(Long userId, SellerRequests.RegisterSellerRequest request) {
        if (sellerRepository.existsByUserId(userId)) {
            throw new SellerAlreadyExistsException(userId);
        }

        Seller seller = new Seller(
                userId,
                request.businessName(),
                request.email(),
                request.phone()
        );

        SellerRequests.BankAccountRequest bankReq = request.bankAccount();
        BankAccount bankAccount = new BankAccount(
                bankReq.accountHolderName(),
                bankReq.bankName(),
                bankReq.country(),
                bankReq.accountNumber(),
                bankReq.routingCode()
        );
        seller.setBankAccount(bankAccount);

        sellerRepository.save(seller);

        eventPublisher.publishSellerCreated(userId);

        return SellerResponses.SellerProfileResponse.from(seller);
    }

    @Transactional(readOnly = true)
    public SellerResponses.SellerProfileResponse getMyProfile(Long userId) {
        Seller seller = sellerRepository.findByUserIdWithBankAccount(userId)
                .orElseThrow(() -> new SellerNotFoundException(userId));
        return SellerResponses.SellerProfileResponse.from(seller);
    }

    @Transactional
    public SellerResponses.BankAccountSummaryResponse updateBankAccount(Long userId, SellerRequests.UpdateBankAccountRequest request) {
        Seller seller = sellerRepository.findByUserIdWithBankAccount(userId)
                .orElseThrow(() -> new SellerNotFoundException(userId));

        if (seller.getBankAccount() == null) {
            BankAccount bankAccount = new BankAccount(
                    request.accountHolderName(),
                    request.bankName(),
                    request.country(),
                    request.accountNumber(),
                    request.routingCode()
            );
            seller.setBankAccount(bankAccount);
        } else {
            seller.getBankAccount().update(
                    request.accountHolderName(),
                    request.bankName(),
                    request.country(),
                    request.accountNumber(),
                    request.routingCode()
            );
        }

        sellerRepository.save(seller);
        return SellerResponses.BankAccountSummaryResponse.from(seller.getBankAccount());
    }

    @Transactional(readOnly = true)
    public SellerResponses.BankInfoResponse getBankInfo(Long sellerId) {
        Seller seller = sellerRepository.findByIdWithBankAccount(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));

        if (seller.getBankAccount() == null) {
            throw new BankAccountNotFoundException(sellerId);
        }

        return SellerResponses.BankInfoResponse.from(seller.getId(), seller.getBankAccount());
    }

    public SellerResponses.SellerInfoResponse getSellerInfo(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
        return SellerResponses.SellerInfoResponse.from(seller);
    }
}