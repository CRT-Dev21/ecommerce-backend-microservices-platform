package com.ecommerce.crtdev.rag_service.application.port.in;

import com.ecommerce.crtdev.rag_service.domain.ProductDocument;

public interface IndexProductUseCase {
    void index(ProductDocument productDocument);
    void delete(String productId);
}
