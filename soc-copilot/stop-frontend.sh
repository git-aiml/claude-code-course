#!/bin/bash
# Stop the Vite development server

echo "Stopping Vite frontend server..."

# Find and kill process on port 5173
PID=$(lsof -ti :5173)

if [ -z "$PID" ]; then
    echo "No process found running on port 5173"
    exit 0
fi

echo "Found process $PID using port 5173"
kill -15 $PID 2>/dev/null

# Wait up to 5 seconds for graceful shutdown
for i in {1..5}; do
    if ! kill -0 $PID 2>/dev/null; then
        echo "✓ Frontend stopped successfully"
        exit 0
    fi
    echo "Waiting for shutdown... ($i/5)"
    sleep 1
done

# Force kill if still running
echo "Process didn't stop gracefully, forcing shutdown..."
kill -9 $PID 2>/dev/null
echo "✓ Frontend forcefully stopped"
