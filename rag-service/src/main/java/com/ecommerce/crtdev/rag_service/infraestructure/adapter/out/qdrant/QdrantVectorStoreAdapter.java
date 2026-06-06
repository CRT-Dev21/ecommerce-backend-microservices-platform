package com.ecommerce.crtdev.rag_service.infraestructure.adapter.out.qdrant;

import com.ecommerce.crtdev.rag_service.application.ports.out.VectorStorePort;
import com.ecommerce.crtdev.rag_service.domain.model.ProductDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

import static io.qdrant.client.ConditionFactory.matchKeyword;
import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

@Component
public class QdrantVectorStoreAdapter implements VectorStorePort {
    private static final String COLLECTION = "products";
    private final QdrantClient qdrantClient;

    public QdrantVectorStoreAdapter(QdrantClient qdrantClient) {
        this.qdrantClient = qdrantClient;
    }

    @Override
    public Mono<Void> upsert(ProductDocument product, float[] embedding) {
        Map<String, JsonWithInt.Value> payload = new HashMap<>();

        payload.put("productId",value(product.productId()));
        payload.put("name",value(product.name()));
        payload.put("description",value(product.description()));
        payload.put("category",value(product.category()));
        payload.put("price",value(product.price()));

        var point = Points.PointStruct.newBuilder()
                .setId(id(UUID.nameUUIDFromBytes(product.productId().getBytes())))
                .setVectors(vectors(toFloatList(embedding)))
                .putAllPayload(payload)
                .build();

        return Mono.<Points.UpdateResult>create(sink -> {
            var future = qdrantClient.upsertAsync(COLLECTION, List.of(point));
            future.addListener(() -> {
                try {
                    sink.success(future.get());
                } catch (Exception e) {
                    sink.error(e);
                }
            }, Runnable::run); // Executes on the same thread completing the future
        }).then();
    }

    @Override
    public Mono<Void> delete(String productId) {
        return Mono.<Points.UpdateResult>create(sink -> {
            var future = qdrantClient.deleteAsync(
                    COLLECTION,
                    Points.Filter.newBuilder()
                            .addMust(matchKeyword("productId", productId))
                            .build()
            );
            future.addListener(() -> {
                try {
                    sink.success(future.get());
                } catch (Exception e) {
                    sink.error(e);
                }
            }, Runnable::run);
        }).then();
    }

    @Override
    public Mono<List<ProductDocument>> findSimilar(float[] queryEmbedding, int topK) {
        return Mono.<List<Points.ScoredPoint>>create(sink -> {
            var future = qdrantClient.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName(COLLECTION)
                            .addAllVector(toFloatList(queryEmbedding))
                            .setLimit(topK)
                            .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true).build())
                            .build()
            );
            future.addListener(() -> {
                try {
                    sink.success(future.get());
                } catch (Exception e) {
                    sink.error(e);
                }
            }, Runnable::run);
        }).map(results -> results.stream()
                .map(r -> new ProductDocument(
                        r.getPayloadOrThrow("productId").getStringValue(),
                        r.getPayloadOrThrow("name").getStringValue(),
                        r.getPayloadOrThrow("description").getStringValue(),
                        r.getPayloadOrThrow("category").getStringValue(),
                        r.getPayloadOrThrow("price").getDoubleValue()
                ))
                .toList()
        );
    }

    private List<Float> toFloatList(float[] arr){
        List<Float> list = new ArrayList<>(arr.length);
        for(float f:arr) list.add(f);
        return list;
    }
}
