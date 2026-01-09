# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the Backend project files
COPY backend/pom.xml .
COPY backend/src ./src

# Copy the Frontend files into the static resources of the Spring Boot application
# allowing them to be served by the backend
COPY faculty_secret_portal ./src/main/resources/static

# Build the application
# skipping tests to ensure build succeeds even if environment variables are missing
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Explicitly expose port 8080 (Render uses environment variable PORT, but this is good documentation)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
