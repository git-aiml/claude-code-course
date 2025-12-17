#!/bin/bash
# Start the Vite development server

echo "Starting Vite development server on port 5173..."
echo "Press Ctrl+C to stop"
echo ""

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR/frontend"
npm run dev
