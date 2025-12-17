#!/bin/bash
# Start both frontend and backend development servers

echo "========================================="
echo "Starting Development Environment"
echo "========================================="
echo ""
echo "Backend will run on: http://localhost:8080"
echo "Frontend will run on: http://localhost:5173"
echo ""
echo "Press Ctrl+C to stop both servers"
echo ""

# Function to clean up background processes on exit
cleanup() {
    echo ""
    echo "Stopping servers..."
    kill 0
    exit
}

trap cleanup SIGINT SIGTERM

# Store the base directory
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

# Start backend in background
cd "$BASE_DIR/backend"
./mvnw spring-boot:run > "$BASE_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "Backend starting (PID: $BACKEND_PID)... Check backend.log for details"

# Give backend a moment to start
sleep 3

# Start frontend in background
cd "$BASE_DIR/frontend"
npm run dev > "$BASE_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "Frontend starting (PID: $FRONTEND_PID)... Check frontend.log for details"

echo ""
echo "Servers are starting up..."
echo "Backend log: soc-copilot/backend.log"
echo "Frontend log: soc-copilot/frontend.log"
echo ""
echo "Waiting for servers (this will run until you press Ctrl+C)..."

# Wait for both processes
wait
