# ===== Build stage =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

COPY storage-service/build.gradle ./storage-service/
COPY storage-service/src ./storage-service/src

RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew

ENV GRADLE_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 \
-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3"

RUN ./gradlew :storage-service:clean :storage-service:build \
-x test \
--no-daemon \
--refresh-dependencies


FROM curlimages/curl:8.8.0 AS otel-agent

USER root

ARG OTEL_JAVA_AGENT_VERSION=2.23.0

RUN mkdir -p /tmp/otel && \
    curl -fL \
    "https://repo.maven.apache.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent-${OTEL_JAVA_AGENT_VERSION}.jar" \
    -o /tmp/otel/opentelemetry-javaagent.jar


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/storage-service/build/libs/*.jar app.jar
COPY --from=otel-agent /tmp/otel/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

EXPOSE 8081
EXPOSE 9090

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar","app.jar"]