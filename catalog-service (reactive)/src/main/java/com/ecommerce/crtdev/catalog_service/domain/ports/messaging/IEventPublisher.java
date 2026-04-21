package com.ecommerce.crtdev.catalog_service.domain.ports.messaging;

import com.ecommerce.crtdev.catalog_service.domain.events.CloudEvent;
import reactor.core.publisher.Mono;

public interface IEventPublisher {
    Mono<Void> publishEvent(String key, CloudEvent<?> event);
}
