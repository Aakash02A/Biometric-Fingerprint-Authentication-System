FROM openjdk:11-jre-slim

WORKDIR /app

# Copy Spring Boot JAR
COPY backend/target/biometric-auth-api.jar app.jar

# Create data directory for SQLite
RUN mkdir -p /app/data

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -version || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
