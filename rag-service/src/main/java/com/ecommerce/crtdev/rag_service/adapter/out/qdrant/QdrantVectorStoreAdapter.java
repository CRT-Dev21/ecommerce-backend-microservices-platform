package com.ecommerce.crtdev.rag_service.adapter.out.qdrant;

import com.ecommerce.crtdev.rag_service.application.port.out.VectorStorePort;
import com.ecommerce.crtdev.rag_service.domain.ProductDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

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
    public void upsert(ProductDocument product, float[] embedding) {
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

        try {
            qdrantClient.upsertAsync(COLLECTION, List.of(point)).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error upserting product in Qdrant: " + product.productId(), e);
        }
    }

    @Override
    public void delete(String productId) {
        try {
            qdrantClient.deleteAsync(
                    COLLECTION,
                    Points.Filter.newBuilder()
                            .addMust(matchKeyword("productId", productId))
                            .build()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error deleting product from Qdrant: " + productId, e);
        }
    }

    @Override
    public List<ProductDocument> findSimilar(float[] queryEmbedding, int topK) {
        try {
            var results = qdrantClient.searchAsync(
                    Points.SearchPoints.newBuilder()
                            .setCollectionName(COLLECTION)
                            .addAllVector(toFloatList(queryEmbedding))
                            .setLimit(topK)
                            .setWithPayload(Points.WithPayloadSelector.newBuilder()
                                    .setEnable(true).build())
                            .build()
            ).get();

            return results.stream()
                    .map(r -> new ProductDocument(
                            r.getPayloadOrThrow("productId").getStringValue(),
                            r.getPayloadOrThrow("name").getStringValue(),
                            r.getPayloadOrThrow("description").getStringValue(),
                            r.getPayloadOrThrow("category").getStringValue(),
                            r.getPayloadOrThrow("price").getDoubleValue()
                    ))
                    .toList();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error searching in Qdrant", e);
        }
    }

    private List<Float> toFloatList(float[] arr){
        List<Float> list = new ArrayList<>(arr.length);

        for(float f:arr) list.add(f);

        return list;
    }
}
