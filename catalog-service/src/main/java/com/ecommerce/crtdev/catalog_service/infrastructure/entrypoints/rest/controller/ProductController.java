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
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/products")
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
    public Mono<Void> createProduct(
            @RequestPart("data") @Valid CreateProductRequest request,
            @RequestPart(value = "image") FilePart image,
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
                image.content()
        );
        return createProductHandler.execute(command);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SELLER')")
    public Mono<Void> updateProduct(
            @PathVariable String productId,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.price()),
                Optional.ofNullable(request.stock()),
                Optional.ofNullable(image).map(FilePart::content)
        );

        return updateProductHandler.execute(command);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public Mono<Void> deleteProduct(@PathVariable String id) {

        return deleteProductHandler.execute(
                new DeleteProductCommand(id)
        );
    }

    @GetMapping("/homepage")
    public Flux<ProductResponse> homepageProducts(
            @RequestParam(defaultValue = "20") int limit
    ) {

        return homepageHandler.execute(
                new GetHomepageProductsQuery(limit)
        );
    }

    @GetMapping("/{id}")
    public Mono<ProductResponse> getProduct(@PathVariable String id) {

        return getProductHandler.execute(
                new GetProductQuery(id)
        );
    }

    @GetMapping("/search")
    public Flux<ProductResponse> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchProductsQuery query = new SearchProductsQuery(
                Optional.ofNullable(q),
                Optional.ofNullable(categoryId),
                Optional.ofNullable(minPrice),
                Optional.ofNullable(maxPrice),
                page,
                size
        );
        return searchHandler.execute(query);
    }
}
