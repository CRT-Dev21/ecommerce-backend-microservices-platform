package com.ecommerce.crtdev.catalog_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    //"mongodb://mongo:mongouser@catalog-db:27017/products_db?authSource=admin&maxPoolSize=100&minPoolSize=10&waitQueueTimeoutMS=5000"
    @Value("${mongodb.env.url}")
    private String mongoUrl;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoUrl);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory databaseFactory) {
        return new MongoTemplate(databaseFactory);
    }
}
