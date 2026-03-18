package com.ecommerce.crtdev.order_service.repository;

import com.ecommerce.crtdev.order_service.entity.ProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshot, String> {
    Optional<ProductSnapshot> findByProductIdAndActiveTrue(String productId);
}
