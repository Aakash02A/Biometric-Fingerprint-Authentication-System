#!/bin/bash
# Installation and Startup Script for Biometric Authentication System

set -e

echo "=========================================="
echo "Biometric Fingerprint Authentication System"
echo "Automated Setup Script"
echo "=========================================="
echo ""

# Check Java
echo "✓ Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "✗ Java not found. Please install Java 11 or higher."
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "  Found: $JAVA_VERSION"

# Check Maven
echo "✓ Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "✗ Maven not found. Please install Maven."
    exit 1
fi
echo "  Found: $(mvn -v | head -1)"

# Build Backend
echo ""
echo "Building Spring Boot API..."
cd backend
mvn clean package -q
echo "✓ Backend build complete"
cd ..

# Create data directory
echo "Creating data directory..."
mkdir -p data
echo "✓ Data directory created"

# Display information
echo ""
echo "=========================================="
echo "Setup Complete!"
echo "=========================================="
echo ""
echo "To start the application:"
echo ""
echo "Option 1 - Direct JAR:"
echo "  java -jar backend/target/biometric-auth-api.jar"
echo ""
echo "Option 2 - Docker Compose:"
echo "  docker-compose up -d"
echo ""
echo "API will be available at:"
echo "  http://localhost:8080/v1"
echo ""
echo "Frontend:"
echo "  Open frontend/index.html in your browser"
echo ""
echo "Documentation:"
echo "  - QUICKSTART.md - Get running in 5 minutes"
echo "  - DEPLOYMENT_GUIDE.md - Production deployment"
echo ""
