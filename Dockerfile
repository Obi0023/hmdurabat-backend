# Use Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose port (matches Render's default port 10000)
EXPOSE 10000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=10000"]
