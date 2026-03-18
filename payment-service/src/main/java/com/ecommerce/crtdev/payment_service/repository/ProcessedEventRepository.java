package com.ecommerce.crtdev.payment_service.repository;

import com.ecommerce.crtdev.payment_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    boolean existsByEventId(UUID eventId);
}