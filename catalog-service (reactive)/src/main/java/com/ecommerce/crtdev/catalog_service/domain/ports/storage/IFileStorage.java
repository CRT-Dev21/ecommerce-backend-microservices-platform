package com.ecommerce.crtdev.catalog_service.domain.ports.storage;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IFileStorage {
    Mono<String> storeImage(Flux<DataBuffer> image);
    Mono<Void> deleteImage(String imageUrl);
}
