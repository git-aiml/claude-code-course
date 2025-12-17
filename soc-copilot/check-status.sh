#!/bin/bash
# Check status of all development services

echo "========================================="
echo "Development Environment Status Check"
echo "========================================="
echo ""

# Check Backend (Port 8080)
echo "🔧 Backend (Spring Boot) - Port 8080:"
if lsof -i :8080 | grep -q LISTEN; then
    echo "   ✓ RUNNING"
    lsof -i :8080 | grep LISTEN | awk '{print "   PID: " $2 ", Process: " $1}'
else
    echo "   ✗ NOT RUNNING"
fi
echo ""

# Check Frontend (Port 5173)
echo "🎨 Frontend (Vite) - Port 5173:"
if lsof -i :5173 | grep -q LISTEN; then
    echo "   ✓ RUNNING"
    lsof -i :5173 | grep LISTEN | awk '{print "   PID: " $2 ", Process: " $1}'
else
    echo "   ✗ NOT RUNNING"
fi
echo ""

# Check Database (Port 5432)
echo "🗄️  Database (PostgreSQL) - Port 5432:"
if lsof -i :5432 | grep -q LISTEN; then
    echo "   ✓ RUNNING"
    lsof -i :5432 | grep LISTEN | awk '{print "   PID: " $2 ", Process: " $1}'

    # Try to connect to database
    if /opt/homebrew/opt/postgresql@16/bin/psql -U sujit -d aisoc_copilot -c "SELECT 1;" > /dev/null 2>&1; then
        echo "   ✓ Database 'aisoc_copilot' is accessible"
    else
        echo "   ⚠ Database connection failed"
    fi
else
    echo "   ✗ NOT RUNNING"
fi
echo ""

# Summary
echo "========================================="
echo "Access URLs:"
echo "  Frontend:  http://localhost:5173"
echo "  Backend:   http://localhost:8080"
echo "  Health:    http://localhost:8080/actuator/health"
echo "========================================="
