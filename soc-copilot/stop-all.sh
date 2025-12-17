#!/bin/bash
# Stop both frontend and backend servers

echo "========================================="
echo "Stopping All Development Servers"
echo "========================================="
echo ""

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"

# Stop backend
"$BASE_DIR/stop-backend.sh"
echo ""

# Stop frontend
"$BASE_DIR/stop-frontend.sh"
echo ""

echo "========================================="
echo "All servers stopped"
echo "========================================="
