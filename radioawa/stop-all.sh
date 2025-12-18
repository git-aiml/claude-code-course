#!/bin/bash

# radioawa - Stop All Services Script

echo "Stopping radioawa application..."
echo "================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Stop Frontend
if [ -f frontend.pid ]; then
    FRONTEND_PID=$(cat frontend.pid)
    echo -e "${YELLOW}Stopping frontend (PID: $FRONTEND_PID)...${NC}"
    kill $FRONTEND_PID 2>/dev/null
    rm frontend.pid
    echo -e "${GREEN}Frontend stopped${NC}"
else
    echo -e "${YELLOW}Frontend PID file not found${NC}"
fi

# Stop Backend
if [ -f backend.pid ]; then
    BACKEND_PID=$(cat backend.pid)
    echo -e "${YELLOW}Stopping backend (PID: $BACKEND_PID)...${NC}"
    kill $BACKEND_PID 2>/dev/null
    rm backend.pid
    echo -e "${GREEN}Backend stopped${NC}"
else
    echo -e "${YELLOW}Backend PID file not found${NC}"
fi

echo -e "\n${GREEN}Application stopped!${NC}"
echo -e "\nNote: PostgreSQL is still running in the background."
echo -e "To stop PostgreSQL: ${YELLOW}brew services stop postgresql@16${NC}"
echo -e "To check status: ${YELLOW}brew services list${NC}"
