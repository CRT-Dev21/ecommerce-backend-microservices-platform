package com.ecommerce.crtdev.seller_service.repository;

import com.ecommerce.crtdev.seller_service.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Query("SELECT s FROM Seller s LEFT JOIN FETCH s.bankAccount WHERE s.userId = :userId")
    Optional<Seller> findByUserIdWithBankAccount(Long userId);
}