# Multi-stage build for ai-agent-service
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
# Copy the whole monorepo to build with -pl
COPY . .
# Build only the ai-agent-service module and its deps
RUN mvn -q -DskipTests -pl ai-agent-service -am package

FROM eclipse-temurin:21-jre
ENV TZ=UTC
WORKDIR /app
# Copy built jar
COPY --from=builder /workspace/ai-agent-service/target/*.jar /app/app.jar
EXPOSE 8081
# Let users override JVM options
ENV JAVA_OPTS=""
# Default profile for containers
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
