#!/bin/bash

# radioawa - Start All Services Script

echo "Starting radioawa application..."
echo "================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if PostgreSQL is running
if ! brew services list | grep -q "postgresql@16.*started"; then
    echo -e "${YELLOW}Starting PostgreSQL...${NC}"
    brew services start postgresql@16
    sleep 3
fi

# Verify PostgreSQL is accessible
if ! /opt/homebrew/opt/postgresql@16/bin/psql -U $USER -d radioawa -c '\q' 2>/dev/null; then
    echo -e "${RED}Error: Cannot connect to PostgreSQL database. Please check if it's running.${NC}"
    echo -e "Try: brew services restart postgresql@16"
    exit 1
fi

echo -e "${GREEN}PostgreSQL is running${NC}"

# Start Backend
echo -e "\n${YELLOW}[1/2] Starting Spring Boot backend...${NC}"
cd backend
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > ../backend.pid
cd ..
echo -e "${GREEN}Backend starting... (PID: $BACKEND_PID)${NC}"
echo -e "Backend logs: tail -f backend.log"

# Wait for backend to start
echo -e "\n${YELLOW}Waiting for backend to be ready...${NC}"
sleep 10

# Start Frontend
echo -e "\n${YELLOW}[2/2] Starting React frontend...${NC}"
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../frontend.pid
cd ..
echo -e "${GREEN}Frontend starting... (PID: $FRONTEND_PID)${NC}"
echo -e "Frontend logs: tail -f frontend.log"

# Summary
echo -e "\n${GREEN}================================${NC}"
echo -e "${GREEN}All services started!${NC}"
echo -e "${GREEN}================================${NC}"
echo -e "\nServices:"
echo -e "  Database:  localhost:5432 (PostgreSQL via Homebrew)"
echo -e "  Backend:   http://localhost:8081 (Spring Boot)"
echo -e "  Frontend:  http://localhost:5171 (React + Vite)"
echo -e "\nOpen your browser: ${GREEN}http://localhost:5171${NC}"
echo -e "\nTo stop all services, run: ${YELLOW}./stop-all.sh${NC}"
echo -e "\nView logs:"
echo -e "  Backend:  tail -f backend.log"
echo -e "  Frontend: tail -f frontend.log"
echo -e "  Database: brew services info postgresql@16"
