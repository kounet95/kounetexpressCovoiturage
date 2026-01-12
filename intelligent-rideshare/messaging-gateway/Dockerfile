# Multi-stage build for messaging-gateway
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
# Copy root and module POMs to leverage caching
COPY pom.xml ./
COPY messaging-gateway/pom.xml messaging-gateway/pom.xml
COPY common/pom.xml common/pom.xml
# Copy sources
COPY . .
# Build only the messaging-gateway module and its deps
RUN mvn -q -DskipTests -pl messaging-gateway -am package

FROM eclipse-temurin:21-jre
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
WORKDIR /app
COPY --from=build /build/messaging-gateway/target/messaging-gateway-*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
