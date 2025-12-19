#!/bin/bash

# radioawa - Test All Script
# Runs all unit tests for both backend and frontend

set -e

echo "=========================================="
echo "radioawa - Running All Tests"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track results
BACKEND_STATUS=0
FRONTEND_STATUS=0

# Run Backend Tests
echo -e "${YELLOW}Running Backend Tests...${NC}"
echo "=========================================="
cd backend
if mvn clean test; then
    echo -e "${GREEN}✅ Backend Tests: PASSED${NC}"
else
    echo -e "${RED}❌ Backend Tests: FAILED${NC}"
    BACKEND_STATUS=1
fi
cd ..
echo ""

# Run Frontend Tests
echo -e "${YELLOW}Running Frontend Tests...${NC}"
echo "=========================================="
cd frontend
if npm run test; then
    echo -e "${GREEN}✅ Frontend Tests: PASSED${NC}"
else
    echo -e "${RED}❌ Frontend Tests: FAILED${NC}"
    FRONTEND_STATUS=1
fi
cd ..
echo ""

# Summary
echo "=========================================="
echo "Test Summary"
echo "=========================================="

if [ $BACKEND_STATUS -eq 0 ] && [ $FRONTEND_STATUS -eq 0 ]; then
    echo -e "${GREEN}✅ All Tests PASSED!${NC}"
    echo ""
    echo "To view coverage reports:"
    echo "  Backend:  mvn jacoco:report && open backend/target/site/jacoco/index.html"
    echo "  Frontend: npm run test:coverage && open frontend/coverage/index.html"
    exit 0
else
    echo -e "${RED}❌ Some Tests FAILED${NC}"
    if [ $BACKEND_STATUS -ne 0 ]; then
        echo "  - Backend tests failed"
    fi
    if [ $FRONTEND_STATUS -ne 0 ]; then
        echo "  - Frontend tests failed"
    fi
    exit 1
fi
