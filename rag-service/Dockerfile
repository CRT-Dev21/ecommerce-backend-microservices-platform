FROM maven:3.9.9-eclipse-temurin-21 AS deps
WORKDIR /build
COPY rag-service/pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

FROM deps AS builder
COPY rag-service/src ./src
RUN mvn clean package -DskipTests -B --no-transfer-progress

WORKDIR /extracted
RUN java -Djarmode=layertools -jar /build/target/*.jar extract --destination /extracted

FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app

COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

ENTRYPOINT [ \
  "java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "org.springframework.boot.loader.launch.JarLauncher" \
]