FROM ubuntu:latest
LABEL authors="quatarnary"

# Use Corretto 21 (Amazon JDK 21)
FROM amazoncorretto:21

# Set working directory inside the container
WORKDIR /app

# Copy the built application JAR from target/ to container
COPY target/*.jar app.jar

# Expose the API port (8080)
EXPOSE 8080

# Set the Postgres
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://excuse-db:5432/excusedb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
