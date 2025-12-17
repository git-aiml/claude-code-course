#!/bin/bash
# Start ALL services including PostgreSQL
# WARNING: This is NOT recommended for normal development!
# Use this only if you specifically want to manage PostgreSQL with your app.

echo "========================================="
echo "Starting ALL Services (Including DB)"
echo "========================================="
echo ""

# Start PostgreSQL
echo "Starting PostgreSQL..."
brew services start postgresql@16
echo "✓ PostgreSQL starting"
echo ""

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
for i in {1..10}; do
    if /opt/homebrew/opt/postgresql@16/bin/pg_isready -q; then
        echo "✓ PostgreSQL is ready"
        break
    fi
    echo "Waiting... ($i/10)"
    sleep 1
done
echo ""

# Start application services
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
"$BASE_DIR/start-dev.sh"
