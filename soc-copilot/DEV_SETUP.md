# Local Development Setup

## Overview
Your local development environment is configured with:
- **Backend**: Spring Boot (Java 21) running on port 8080
- **Frontend**: React + Vite running on port 5173
- **Database**: PostgreSQL 16 running on port 5432

## Prerequisites (Already Installed)
- ✓ Java 21
- ✓ Maven (via mvnw wrapper)
- ✓ Node.js v25.2.1
- ✓ npm v11.6.2
- ✓ PostgreSQL 16

## Database Configuration
Database: `aisoc_copilot`
User: `sujit`
Host: `localhost:5432`

PostgreSQL is already running as a service via Homebrew.

## Starting the Development Environment

### Quick Start - All Services
```bash
cd soc-copilot
./start-dev.sh
```

### Individual Services

#### Start Backend
```bash
cd soc-copilot
./start-backend.sh
```
The backend will be available at http://localhost:8080

Default credentials:
- Username: `admin`
- Password: `admin123`

#### Start Frontend
```bash
cd soc-copilot
./start-frontend.sh
```
The frontend will be available at http://localhost:5173

## Stopping Services

### Stop All Services
```bash
cd soc-copilot
./stop-all.sh
```

### Stop Individual Services
```bash
# Stop backend only
./stop-backend.sh

# Stop frontend only
./stop-frontend.sh
```

### Emergency Port Cleanup
If a port is stuck (e.g., "Port 8080 already in use"):
```bash
# Check all development ports
./port-utils.sh check

# Kill process on specific port
./port-utils.sh kill 8080
./port-utils.sh kill 5173

# Or check individual services
./port-utils.sh backend
./port-utils.sh frontend
```

## Useful Commands

### Database Management
```bash
# Connect to database
/opt/homebrew/opt/postgresql@16/bin/psql -U sujit -d aisoc_copilot

# List tables
/opt/homebrew/opt/postgresql@16/bin/psql -U sujit -d aisoc_copilot -c "\dt"

# Check PostgreSQL service status
brew services list | grep postgres

# Restart PostgreSQL
brew services restart postgresql@16
```

### Backend Commands
```bash
cd soc-copilot/backend

# Clean and build
./mvnw clean package

# Run tests
./mvnw test

# Clean compile
./mvnw clean compile
```

### Frontend Commands
```bash
cd soc-copilot/frontend

# Install dependencies (if needed)
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Run linter
npm run lint

# Preview production build
npm run preview
```

## API Endpoints
- Health Check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

## Database Schema
Current tables:
- `alerts` - Alert management table

## Environment Files
Backend configuration: `soc-copilot/backend/src/main/resources/application.properties`
Frontend configuration: `soc-copilot/frontend/vite.config.ts`

## Troubleshooting

### Backend won't start - "Port 8080 already in use"
**This is the most common issue!** When you stop the backend with Ctrl+C, the Java process may not terminate properly.

**Solution:**
```bash
# Quick fix - Stop the backend properly
./stop-backend.sh

# Or check and kill manually
./port-utils.sh backend
./port-utils.sh kill 8080

# Or find and kill the process
lsof -i :8080
kill -9 <PID>
```

**Prevention:**
- Always use `./stop-backend.sh` to stop the backend
- Don't just press Ctrl+C (it may leave the process running)
- Use `./port-utils.sh check` before starting servers

### Frontend won't start - "Port 5173 already in use"
```bash
# Stop frontend properly
./stop-frontend.sh

# Or kill manually
./port-utils.sh kill 5173
```

### Database connection errors
```bash
# Check if PostgreSQL is running
brew services list | grep postgres

# Verify database exists
./db-utils.sh info

# Test connection
./db-utils.sh connect
```

### General debugging
```bash
# Check status of all services
./check-status.sh

# Check all ports at once
./port-utils.sh check

# View backend logs (if using start-dev.sh)
tail -f backend.log

# View frontend logs (if using start-dev.sh)
tail -f frontend.log
```

### Reinstall dependencies
```bash
# Frontend
cd soc-copilot/frontend
rm -rf node_modules package-lock.json
npm install

# Backend
cd soc-copilot/backend
./mvnw clean install
```
