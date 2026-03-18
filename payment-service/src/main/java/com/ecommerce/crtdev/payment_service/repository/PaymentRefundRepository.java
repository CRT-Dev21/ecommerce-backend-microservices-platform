package com.ecommerce.crtdev.payment_service.repository;

import com.ecommerce.crtdev.payment_service.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, String> {
    Optional<PaymentRefund> findByOrderId(String orderId);
}