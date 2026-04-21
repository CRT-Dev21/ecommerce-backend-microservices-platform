package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.repository;

import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.infrastructure.mappers.ProductEntitiesMapper;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.mongo.ProductDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MongoProductRepository implements IProductRepository {

    private final MongoTemplate mongoTemplate;

    public MongoProductRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Product save(Product product) {
        ProductDocument doc = ProductEntitiesMapper.domainToDocument(product);
        ProductDocument saved = mongoTemplate.save(doc);
        return ProductEntitiesMapper.documentToDomain(saved);
    }

    @Override
    public Optional<Product> findById(String id) {
        ProductDocument doc = mongoTemplate.findById(id, ProductDocument.class);
        return Optional.ofNullable(doc).map(ProductEntitiesMapper::documentToDomain);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, ProductDocument.class);
    }

    @Override
    public List<Product> search(SearchProductsQuery query) {
        Query mongoQuery = new Query();

        query.searchTerm().ifPresent(term -> {
            mongoQuery.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(term, "i"),
                    Criteria.where("description").regex(term, "i")
            ));
        });

        query.categoryId().ifPresent(cat ->
                mongoQuery.addCriteria(Criteria.where("categoryId").is(cat)));

        if (query.minPrice().isPresent() || query.maxPrice().isPresent()) {
            Criteria priceCriteria = Criteria.where("price");
            query.minPrice().ifPresent(priceCriteria::gte);
            query.maxPrice().ifPresent(priceCriteria::lte);
            mongoQuery.addCriteria(priceCriteria);
        }

        mongoQuery.with(PageRequest.of(query.page(), query.size()));

        return mongoTemplate.find(mongoQuery, ProductDocument.class)
                .stream()
                .map(ProductEntitiesMapper::documentToDomain)
                .toList();
    }

    @Override
    public List<Product> findLatestProducts(int limit) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "_id"))
                .limit(limit);

        return mongoTemplate.find(query, ProductDocument.class)
                .stream()
                .map(ProductEntitiesMapper::documentToDomain)
                .toList();
    }

    @Override
    public List<Product> findByCategory(String categoryId) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        return mongoTemplate.find(query, ProductDocument.class)
                .stream()
                .map(ProductEntitiesMapper::documentToDomain)
                .toList();
    }
}