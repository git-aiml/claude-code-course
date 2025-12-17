#!/bin/bash
# Stop the Spring Boot backend server

echo "Stopping Spring Boot backend..."

# Find and kill process on port 8080
PID=$(lsof -ti :8080)

if [ -z "$PID" ]; then
    echo "No process found running on port 8080"
    exit 0
fi

echo "Found process $PID using port 8080"
kill -15 $PID 2>/dev/null

# Wait up to 10 seconds for graceful shutdown
for i in {1..10}; do
    if ! kill -0 $PID 2>/dev/null; then
        echo "✓ Backend stopped successfully"
        exit 0
    fi
    echo "Waiting for shutdown... ($i/10)"
    sleep 1
done

# Force kill if still running
echo "Process didn't stop gracefully, forcing shutdown..."
kill -9 $PID 2>/dev/null
echo "✓ Backend forcefully stopped"
