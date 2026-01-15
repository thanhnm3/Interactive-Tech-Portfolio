# Dockerfile for Portfolio Backend Application
# Multi-stage build for optimized image size

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Add non-root user for security
RUN addgroup -S portfolio && adduser -S portfolio -G portfolio

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership
RUN chown -R portfolio:portfolio /app

# Switch to non-root user
USER portfolio

# Expose port
EXPOSE 8088

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8088/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
