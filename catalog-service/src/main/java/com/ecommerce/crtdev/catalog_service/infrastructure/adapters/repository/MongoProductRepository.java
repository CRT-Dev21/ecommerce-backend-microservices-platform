package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.repository;

import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.infrastructure.mappers.ProductEntitiesMapper;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.mongo.ProductDocument;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
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
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ProductDocument.class);
    }

    @Override
    public List<Product> search(SearchProductsQuery query) {
        Query mongoQuery = new Query();

        query.searchTerm()
                .filter(t -> !t.isBlank())
                .ifPresent(term ->
                        mongoQuery.addCriteria(
                                TextCriteria.forDefaultLanguage().matchingPhrase(term)
                        )
                );

        query.categoryId().ifPresent(cat ->
                mongoQuery.addCriteria(Criteria.where("categoryId").is(cat)));

        if (query.minPrice().isPresent() || query.maxPrice().isPresent()) {
            Criteria price = Criteria.where("price");
            query.minPrice().ifPresent(price::gte);
            query.maxPrice().ifPresent(price::lte);
            mongoQuery.addCriteria(price);
        }

        query.lastId().ifPresent(lastId ->
                mongoQuery.addCriteria(Criteria.where("_id").gt(new ObjectId(lastId))));

        mongoQuery.fields()
                .include("name")
                .include("price")
                .include("categoryId")
                .include("stock")
                .include("imageUrl")
                .include("sellerId");

        mongoQuery
                .with(Sort.by(Sort.Direction.ASC, "_id"))
                .limit(query.size());

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

        query.fields()
                .include("name")
                .include("price")
                .include("imageUrl")
                .include("categoryId");

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