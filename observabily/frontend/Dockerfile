# Stage 1: Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./

# Cache dependencies (empty build to force cache dependencies)
RUN gradle build --no-daemon -x test || true

# Copy source code and build jar
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/frontend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
