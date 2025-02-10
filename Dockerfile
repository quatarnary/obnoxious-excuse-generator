FROM ubuntu:latest
LABEL authors="quatarnary"

# ---- Stage 1: Build the application ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only pom.xml and download dependencies first (caching layer)
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Stage 2: Run the application ----
# Use Corretto 21 (Amazon JDK 21)
FROM amazoncorretto:21

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR from the first stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the API port (8080)
EXPOSE 8080

# Set the Postgres
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://excuse-db:5432/excusedb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
