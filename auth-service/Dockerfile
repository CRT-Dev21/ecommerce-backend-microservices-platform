FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY auth-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY auth-service/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd -ms /bin/bash springuser
USER springuser

COPY --from=builder /build/target/*.jar app.jar

COPY auth-service/rsa/private.pem certs/
COPY auth-service/rsa/public.pem certs/

ENTRYPOINT ["java", "-jar", "app.jar"]