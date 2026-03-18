FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY catalog-service ./catalog-service
RUN mvn -f ./catalog-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser

RUN mkdir -p /app/product/image && chown -R springuser:springuser /app/product

USER springuser

COPY --from=builder /build/catalog-service/target/catalog-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]