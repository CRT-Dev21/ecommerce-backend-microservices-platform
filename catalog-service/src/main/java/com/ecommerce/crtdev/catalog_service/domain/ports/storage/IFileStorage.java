package com.ecommerce.crtdev.catalog_service.domain.ports.storage;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorage {
    String storeImage(MultipartFile image);
    void deleteImage(String imageUrl);
}
