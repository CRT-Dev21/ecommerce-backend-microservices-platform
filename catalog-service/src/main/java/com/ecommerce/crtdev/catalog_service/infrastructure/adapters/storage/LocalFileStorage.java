package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.storage;

import com.ecommerce.crtdev.catalog_service.domain.ports.storage.IFileStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalFileStorage implements IFileStorage {
    private final Path root = Path.of("/app/products/images");

    public LocalFileStorage () {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String storeImage(MultipartFile image) {
        String fileName = UUID.randomUUID().toString();
        Path targetPath = this.root.resolve(fileName).normalize().toAbsolutePath();

        if (!targetPath.startsWith(root)) throw new RuntimeException("Invalid path");

        try {
            image.transferTo(targetPath);
            return targetPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error storing image", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            Files.deleteIfExists(Path.of(imageUrl));
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image");
        }
    }
}
