#!/bin/bash
# Port utility commands for managing development servers

case "$1" in
    check)
        echo "Checking development server ports..."
        echo ""
        echo "Port 8080 (Backend):"
        lsof -i :8080 || echo "  ✓ Available"
        echo ""
        echo "Port 5173 (Frontend):"
        lsof -i :5173 || echo "  ✓ Available"
        echo ""
        echo "Port 5432 (Database):"
        lsof -i :5432 || echo "  ✓ Available"
        ;;
    kill)
        if [ -z "$2" ]; then
            echo "Usage: ./port-utils.sh kill <port>"
            echo "Example: ./port-utils.sh kill 8080"
            exit 1
        fi
        PORT=$2
        PID=$(lsof -ti :$PORT)
        if [ -z "$PID" ]; then
            echo "No process found on port $PORT"
        else
            echo "Killing process $PID on port $PORT..."
            kill -9 $PID
            echo "✓ Process killed"
        fi
        ;;
    backend)
        PID=$(lsof -ti :8080)
        if [ -z "$PID" ]; then
            echo "✗ Backend not running on port 8080"
        else
            echo "✓ Backend running on port 8080 (PID: $PID)"
            lsof -i :8080
        fi
        ;;
    frontend)
        PID=$(lsof -ti :5173)
        if [ -z "$PID" ]; then
            echo "✗ Frontend not running on port 5173"
        else
            echo "✓ Frontend running on port 5173 (PID: $PID)"
            lsof -i :5173
        fi
        ;;
    *)
        echo "Port Utility Commands"
        echo "====================="
        echo ""
        echo "Usage: ./port-utils.sh [command] [args]"
        echo ""
        echo "Commands:"
        echo "  check              - Check status of all development ports"
        echo "  kill <port>        - Kill process using specified port"
        echo "  backend            - Check backend port (8080)"
        echo "  frontend           - Check frontend port (5173)"
        echo ""
        echo "Examples:"
        echo "  ./port-utils.sh check"
        echo "  ./port-utils.sh kill 8080"
        echo "  ./port-utils.sh backend"
        echo ""
        ;;
esac
