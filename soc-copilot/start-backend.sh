#!/bin/bash
# Start the Spring Boot backend server

echo "Starting Spring Boot backend on port 8080..."
echo "Press Ctrl+C to stop"
echo ""

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR/backend"
./mvnw spring-boot:run
