package com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.controller;

import com.ecommerce.crtdev.catalog_service.application.commands.CreateProductCommand;
import com.ecommerce.crtdev.catalog_service.application.commands.DeleteProductCommand;
import com.ecommerce.crtdev.catalog_service.application.commands.UpdateProductCommand;
import com.ecommerce.crtdev.catalog_service.application.handlers.*;
import com.ecommerce.crtdev.catalog_service.application.queries.GetHomepageProductsQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.GetProductQuery;
import com.ecommerce.crtdev.catalog_service.application.queries.ProductResponse;
import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto.CreateProductRequest;
import com.ecommerce.crtdev.catalog_service.infrastructure.entrypoints.rest.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalog/products")
@Validated
public class ProductController {

    private final CreateProductHandler createProductHandler;
    private final DeleteProductHandler deleteProductHandler;
    private final UpdateProductHandler updateProductHandler;
    private final GetHomepageProductsHandler homepageHandler;
    private final GetProductHandler getProductHandler;
    private final SearchProductsHandler searchHandler;

    public ProductController(
            CreateProductHandler createProductHandler,
            DeleteProductHandler deleteProductHandler,
            UpdateProductHandler updateProductHandler,
            GetHomepageProductsHandler homepageHandler,
            GetProductHandler getProductHandler,
            SearchProductsHandler searchHandler
    ) {
        this.createProductHandler = createProductHandler;
        this.deleteProductHandler = deleteProductHandler;
        this.updateProductHandler = updateProductHandler;
        this.homepageHandler = homepageHandler;
        this.getProductHandler = getProductHandler;
        this.searchHandler = searchHandler;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("data") @Valid CreateProductRequest request,
            @RequestPart(value = "image") MultipartFile image,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long sellerId = Long.parseLong(jwt.getSubject());

        CreateProductCommand command = new CreateProductCommand(
                sellerId,
                request.name(),
                request.description(),
                request.price(),
                request.categoryId(),
                request.stock(),
                image
        );

        ProductResponse newProduct = createProductHandler.execute(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newProduct.productId())
                .toUri();

        return ResponseEntity.created(location).body(newProduct);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.price()),
                Optional.ofNullable(request.stock()),
                Optional.ofNullable(image)
        );

        ProductResponse updatedProduct = updateProductHandler.execute(command);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {

        deleteProductHandler.execute(new DeleteProductCommand(id));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/homepage")
    public ResponseEntity<List<ProductResponse>> homepageProducts(
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit
    ) {
        List<ProductResponse> products =  homepageHandler.execute(new GetHomepageProductsQuery(limit));

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {

        ProductResponse product = getProductHandler.execute(
                new GetProductQuery(id)
        );

        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        SearchProductsQuery query = new SearchProductsQuery(
                Optional.ofNullable(searchTerm),
                Optional.ofNullable(categoryId),
                Optional.ofNullable(minPrice),
                Optional.ofNullable(maxPrice),
                Optional.ofNullable(lastId),
                size
        );

        List<ProductResponse> products = searchHandler.execute(query);

        return ResponseEntity.ok(products);
    }
}
