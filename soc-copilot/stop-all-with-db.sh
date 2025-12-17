#!/bin/bash
# Stop ALL services including PostgreSQL
# WARNING: This is NOT recommended for normal development!
# Use this only if you specifically want to manage PostgreSQL with your app.

echo "========================================="
echo "Stopping ALL Services (Including DB)"
echo "========================================="
echo ""

# Stop application services first
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
"$BASE_DIR/stop-all.sh"
echo ""

# Stop PostgreSQL
echo "Stopping PostgreSQL..."
brew services stop postgresql@16
echo "✓ PostgreSQL stopped"
echo ""

echo "========================================="
echo "All services stopped (including database)"
echo "========================================="
echo ""
echo "⚠️  WARNING: PostgreSQL is now stopped!"
echo "To start it again: brew services start postgresql@16"
echo "Or use: ./start-all-with-db.sh"
