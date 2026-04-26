FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY rag-service ./rag-service
RUN mvn -f ./rag-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=builder /build/rag-service/target/rag-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]