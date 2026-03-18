FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY seller-service ./seller-service
RUN mvn -f ./seller-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=builder /build/seller-service/target/seller-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]