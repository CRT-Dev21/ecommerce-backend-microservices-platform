package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.storage;

import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class LocalFileStorage implements IFileStorage {
    private final Path root = Path.of("/app/product/image");

    @Override
    public Mono<String> storeImage(Flux<DataBuffer> imageData) {
        String imageId = UUID.randomUUID().toString() + ".jpg";
        Path imagePath = root.resolve(imageId);

        return DataBufferUtils.write(imageData, imagePath, StandardOpenOption.CREATE)
                .checkpoint("Escritura de imagen " + imageId)
                .then(Mono.just("http://localhost:8081/products/image/" + imageId));
    }

    @Override
    public Mono<Void> deleteImage(String imageUrl) {
        return Mono.fromRunnable(() -> {
            try {
                if(imageUrl != null){
                    Files.deleteIfExists(Path.of(imageUrl));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
