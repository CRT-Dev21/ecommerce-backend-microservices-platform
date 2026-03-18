package com.ecommerce.crtdev.payment_service.repository;

import com.ecommerce.crtdev.payment_service.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
    List<PaymentMethod> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<PaymentMethod> findByIdAndUserId(String id, Long userId);
}