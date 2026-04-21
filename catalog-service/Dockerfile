FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY catalog-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY catalog-service/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

USER root
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN mkdir -p /app/products/images && chown -R spring:spring /app

USER spring:spring

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseStringDeduplication", "-jar", "app.jar"]