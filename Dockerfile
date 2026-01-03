# ====================================
# STAGE 1: Application Build
# ====================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Working directory
WORKDIR /app

# Copy only dependency files first (leverages Docker cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skips tests for faster build)
RUN mvn clean package -DskipTests

# ====================================
# STAGE 2: Runtime Image
# ====================================
FROM eclipse-temurin:21-jre-alpine

# Add non-root user for security (best practice!)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Working directory
WORKDIR /app

# Copy only the application JAR from previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Configure JVM for containers (important!)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
