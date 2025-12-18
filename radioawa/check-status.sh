#!/bin/bash

# radioawa - Check Status of All Services

echo "Checking radioawa Services Status..."
echo "===================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check PostgreSQL
echo -n "PostgreSQL Database: "
if brew services list | grep -q "postgresql@16.*started"; then
    echo -e "${GREEN}✓ Running${NC}"
    # Try to connect
    if /opt/homebrew/opt/postgresql@16/bin/psql -U radioawa -d radioawa -c '\q' 2>/dev/null; then
        echo "  └─ Database 'radioawa' is accessible"
    else
        echo -e "  └─ ${YELLOW}Service running but database may not be accessible${NC}"
    fi
else
    echo -e "${RED}✗ Not Running${NC}"
    echo "  └─ Start with: brew services start postgresql@16"
fi
echo ""

# Check Backend
echo -n "Spring Boot Backend: "
if [ -f backend.pid ]; then
    BACKEND_PID=$(cat backend.pid)
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Running (PID: $BACKEND_PID)${NC}"
        # Check if port 8081 is responding
        if curl -s http://localhost:8081/api/health > /dev/null 2>&1; then
            echo "  └─ API responding at http://localhost:8081"
        else
            echo -e "  └─ ${YELLOW}Process running but API not responding yet${NC}"
        fi
    else
        echo -e "${RED}✗ Not Running (stale PID file)${NC}"
        echo "  └─ Start with: cd backend && mvn spring-boot:run"
    fi
else
    echo -e "${RED}✗ Not Running${NC}"
    echo "  └─ Start with: cd backend && mvn spring-boot:run"
fi
echo ""

# Check Frontend
echo -n "React Frontend: "
if [ -f frontend.pid ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Running (PID: $FRONTEND_PID)${NC}"
        # Check if port 5171 is responding
        if curl -s http://localhost:5171 > /dev/null 2>&1; then
            echo "  └─ App available at http://localhost:5171"
        else
            echo -e "  └─ ${YELLOW}Process running but not responding yet${NC}"
        fi
    else
        echo -e "${RED}✗ Not Running (stale PID file)${NC}"
        echo "  └─ Start with: cd frontend && npm run dev"
    fi
else
    echo -e "${RED}✗ Not Running${NC}"
    echo "  └─ Start with: cd frontend && npm run dev"
fi
echo ""

echo "===================================="
echo "Quick commands:"
echo "  Start all:  ./start-all.sh"
echo "  Stop all:   ./stop-all.sh"
echo "  Check logs: tail -f backend.log  OR  tail -f frontend.log"
