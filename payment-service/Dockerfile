FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY payment-service ./payment-service
RUN mvn -f ./payment-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=builder /build/payment-service/target/payment_service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]