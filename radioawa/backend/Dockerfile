# Multi-stage Dockerfile for RadioAwa Backend
# Supports both development and production builds

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Development stage
FROM maven:3.9-eclipse-temurin-17 AS development
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Expose backend port
EXPOSE 8081

# Run with Spring Boot DevTools for hot reload
CMD ["mvn", "spring-boot:run"]

# Stage 3: Production stage
FROM eclipse-temurin:17-jre AS production
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S radioawa && adduser -S radioawa -G radioawa

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R radioawa:radioawa /app

# Switch to non-root user
USER radioawa

# Expose backend port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]
