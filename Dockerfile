# syntax=docker/dockerfile:1
# 1. Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ensure data directory exists for H2 and logs
RUN mkdir -p /app/data

# Copy built artifact
COPY --from=builder /app/target/sistema-matriculas-*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
