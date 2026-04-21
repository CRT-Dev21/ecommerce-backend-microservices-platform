package com.ecommerce.crtdev.catalog_service.domain.exception;

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(String productId){
        super(String.format("Product %s not found", productId), "CATALOG_PRODUCT_NOT_FOUND");
    }
}
