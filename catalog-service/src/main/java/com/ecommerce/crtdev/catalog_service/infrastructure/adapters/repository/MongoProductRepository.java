package com.ecommerce.crtdev.catalog_service.infrastructure.adapters.repository;

import com.ecommerce.crtdev.catalog_service.application.queries.SearchProductsQuery;
import com.ecommerce.crtdev.catalog_service.domain.model.Product;
import com.ecommerce.crtdev.catalog_service.domain.ports.repository.IProductRepository;
import com.ecommerce.crtdev.catalog_service.infrastructure.mappers.ProductEntitiesMapper;
import com.ecommerce.crtdev.catalog_service.infrastructure.persistence.mongo.ProductDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoProductRepository implements IProductRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public MongoProductRepository(ReactiveMongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Product> save(Product product) {

        ProductDocument doc = ProductEntitiesMapper.domainToDocument(product);

        return mongoTemplate.save(doc)
                .map(ProductEntitiesMapper::documentToDomain);
    }

    @Override
    public Mono<Product> findById(String id) {

        return mongoTemplate.findById(id, ProductDocument.class)
                .map(ProductEntitiesMapper::documentToDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {

        Query query = new Query(Criteria.where("id").is(id));

        return mongoTemplate.remove(query, ProductDocument.class)
                .then();
    }

    @Override
    public Flux<Product> search(SearchProductsQuery query) {

        Query mongoQuery = new Query();

        query.searchTerm().ifPresent(term -> {
            Criteria nameOrDesc = new Criteria().orOperator(
                    Criteria.where("name").regex(term, "i"),
                    Criteria.where("description").regex(term, "i")
            );
            mongoQuery.addCriteria(nameOrDesc);
        });

        query.categoryId().ifPresent(category ->
                mongoQuery.addCriteria(Criteria.where("categoryId").is(category))
        );

        if(query.minPrice().isPresent() || query.maxPrice().isPresent()) {

            Criteria priceCriteria = Criteria.where("price");

            query.minPrice().ifPresent(priceCriteria::gte);
            query.maxPrice().ifPresent(priceCriteria::lte);

            mongoQuery.addCriteria(priceCriteria);
        }

        mongoQuery.with(PageRequest.of(query.page(), query.size()));

        return mongoTemplate.find(mongoQuery, ProductDocument.class)
                .map(ProductEntitiesMapper::documentToDomain);
    }

    @Override
    public Flux<Product> findByCategory(String categoryId) {

        Query query = new Query(
                Criteria.where("categoryId").is(categoryId)
        );

        return mongoTemplate.find(query, ProductDocument.class)
                .map(ProductEntitiesMapper::documentToDomain);
    }

    @Override
    public Flux<Product> findLatestProducts(int limit) {

        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "_id"))
                .limit(limit);

        return mongoTemplate.find(query, ProductDocument.class)
                .map(ProductEntitiesMapper::documentToDomain);
    }
}
