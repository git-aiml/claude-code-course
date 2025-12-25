#!/bin/bash

# radioawa - Local Database Setup Script
# Sets up PostgreSQL database for local development

echo "RadioAwa - Local Database Setup"
echo "================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DB_NAME="radioawa"
DB_USER="radioawa"
DB_PASSWORD="radioawa_dev_password"
POSTGRES_BIN="/opt/homebrew/opt/postgresql@16/bin"
MIGRATION_SCRIPT="./backend/multi-station-migration.sql"

# Check if PostgreSQL is installed
if [ ! -d "$POSTGRES_BIN" ]; then
    echo -e "${RED}Error: PostgreSQL 16 not found at $POSTGRES_BIN${NC}"
    echo -e "${YELLOW}Install with: brew install postgresql@16${NC}"
    exit 1
fi

# Check if PostgreSQL is running
echo -e "${BLUE}[1/6] Checking PostgreSQL service...${NC}"
if ! brew services list | grep -q "postgresql@16.*started"; then
    echo -e "${YELLOW}PostgreSQL is not running. Starting it now...${NC}"
    brew services start postgresql@16
    echo -e "${YELLOW}Waiting for PostgreSQL to start...${NC}"
    sleep 5
else
    echo -e "${GREEN}✓ PostgreSQL is running${NC}"
fi

# Verify we can connect to PostgreSQL as superuser
echo -e "\n${BLUE}[2/6] Verifying PostgreSQL connection...${NC}"
if ! $POSTGRES_BIN/psql -U $USER -d postgres -c '\q' 2>/dev/null; then
    echo -e "${RED}Error: Cannot connect to PostgreSQL as user '$USER'${NC}"
    echo -e "${YELLOW}Try: brew services restart postgresql@16${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Connected to PostgreSQL${NC}"

# Create database user if it doesn't exist
echo -e "\n${BLUE}[3/6] Creating database user '$DB_USER'...${NC}"
$POSTGRES_BIN/psql -U $USER -d postgres <<EOF 2>&1 | grep -v "already exists" || true
DO \$\$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = '$DB_USER') THEN
        CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
        RAISE NOTICE 'User $DB_USER created successfully';
    ELSE
        RAISE NOTICE 'User $DB_USER already exists';
    END IF;
END
\$\$;
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ User '$DB_USER' ready${NC}"
else
    echo -e "${RED}Error creating user${NC}"
    exit 1
fi

# Create database if it doesn't exist
echo -e "\n${BLUE}[4/6] Creating database '$DB_NAME'...${NC}"
if $POSTGRES_BIN/psql -U $USER -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
    echo -e "${YELLOW}Database '$DB_NAME' already exists${NC}"
    echo -e "${YELLOW}Do you want to drop and recreate it? This will DELETE ALL DATA! (y/N)${NC}"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Dropping database '$DB_NAME'...${NC}"
        $POSTGRES_BIN/psql -U $USER -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;" 2>/dev/null
        echo -e "${YELLOW}Creating fresh database '$DB_NAME'...${NC}"
        $POSTGRES_BIN/psql -U $USER -d postgres -c "CREATE DATABASE $DB_NAME OWNER $DB_USER;" 2>/dev/null
    else
        echo -e "${GREEN}Keeping existing database${NC}"
    fi
else
    $POSTGRES_BIN/psql -U $USER -d postgres -c "CREATE DATABASE $DB_NAME OWNER $DB_USER;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Database '$DB_NAME' created${NC}"
    else
        echo -e "${RED}Error creating database${NC}"
        exit 1
    fi
fi

# Grant privileges
echo -e "\n${BLUE}[5/6] Granting privileges...${NC}"
$POSTGRES_BIN/psql -U $USER -d postgres <<EOF 2>/dev/null
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;
EOF
echo -e "${GREEN}✓ Privileges granted${NC}"

# Run migration script
echo -e "\n${BLUE}[6/6] Running database migration script...${NC}"
if [ ! -f "$MIGRATION_SCRIPT" ]; then
    echo -e "${RED}Error: Migration script not found at $MIGRATION_SCRIPT${NC}"
    exit 1
fi

PGPASSWORD=$DB_PASSWORD $POSTGRES_BIN/psql -U $DB_USER -d $DB_NAME -f "$MIGRATION_SCRIPT" 2>&1 | grep -v "already exists" | grep -v "NOTICE"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Migration completed${NC}"
else
    echo -e "${RED}Warning: Migration may have failed. Check output above.${NC}"
fi

# Verify setup
echo -e "\n${BLUE}Verifying setup...${NC}"
STATION_COUNT=$(PGPASSWORD=$DB_PASSWORD $POSTGRES_BIN/psql -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM stations;" 2>/dev/null | xargs)

if [ "$STATION_COUNT" -ge 2 ]; then
    echo -e "${GREEN}✓ Setup verification passed${NC}"
else
    echo -e "${YELLOW}⚠ Warning: Expected at least 2 stations, found $STATION_COUNT${NC}"
fi

# Display station info
echo -e "\n${GREEN}================================${NC}"
echo -e "${GREEN}Database Setup Complete!${NC}"
echo -e "${GREEN}================================${NC}"
echo -e "\nConfiguration:"
echo -e "  Database:  $DB_NAME"
echo -e "  User:      $DB_USER"
echo -e "  Password:  $DB_PASSWORD"
echo -e "  Host:      localhost"
echo -e "  Port:      5432"

echo -e "\n${BLUE}Stations installed:${NC}"
PGPASSWORD=$DB_PASSWORD $POSTGRES_BIN/psql -U $DB_USER -d $DB_NAME -c "SELECT code, name, is_active FROM stations ORDER BY display_order;" 2>/dev/null

echo -e "\n${GREEN}Next step: Start the application${NC}"
echo -e "  ${YELLOW}./start-all.sh${NC}"

echo -e "\n${BLUE}Useful commands:${NC}"
echo -e "  Connect to database:    ${YELLOW}PGPASSWORD=$DB_PASSWORD psql -U $DB_USER -d $DB_NAME${NC}"
echo -e "  View stations:          ${YELLOW}SELECT * FROM stations;${NC}"
echo -e "  View songs:             ${YELLOW}SELECT * FROM songs LIMIT 10;${NC}"
echo -e "  View ratings:           ${YELLOW}SELECT * FROM ratings LIMIT 10;${NC}"
echo ""