# Multi-stage build for document-service
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -q -DskipTests -pl document-service -am package

FROM eclipse-temurin:21-jre
ENV TZ=UTC
WORKDIR /app
COPY --from=builder /workspace/document-service/target/*.jar /app/app.jar
EXPOSE 8086
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
