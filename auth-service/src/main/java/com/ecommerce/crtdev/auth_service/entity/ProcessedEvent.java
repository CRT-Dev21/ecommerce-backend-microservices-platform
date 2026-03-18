package com.ecommerce.crtdev.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    public ProcessedEvent(String eventId){
        this.eventId = eventId;
        this.processedAt = Instant.now();
    }

    public ProcessedEvent(){}

    public String getEventId(){
        return eventId;
    }
}
