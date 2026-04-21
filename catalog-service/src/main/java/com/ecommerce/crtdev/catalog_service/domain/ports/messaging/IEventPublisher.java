package com.ecommerce.crtdev.catalog_service.domain.ports.messaging;

import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;

public interface IEventPublisher {
    void publishEvent(String key, CloudEvent<?> event);
}
