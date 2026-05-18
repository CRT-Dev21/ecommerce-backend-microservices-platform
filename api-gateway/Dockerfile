FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

COPY api-gateway/pom.xml .
RUN mvn dependency:go-offline -B

COPY api-gateway/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN addgroup --system javauser && adduser --system --group javauser
USER javauser

COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]