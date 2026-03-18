FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY cart-service ./cart-service
RUN mvn -f ./cart-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=builder /build/cart-service/target/cart-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]