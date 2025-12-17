# Quick Reference Guide

## 🚀 Starting Services

```bash
cd soc-copilot

# Start frontend & backend
./start-dev.sh

# Start individually
./start-backend.sh    # Backend on port 8080
./start-frontend.sh   # Frontend on port 5173

# PostgreSQL is already running - no need to start!
```

## 🛑 Stopping Services

```bash
# Stop frontend & backend
./stop-all.sh

# Stop individually
./stop-backend.sh     # Stop backend gracefully
./stop-frontend.sh    # Stop frontend gracefully

# PostgreSQL stays running - recommended!
# See DATABASE_MANAGEMENT.md for details
```

## ✅ Checking Status

```bash
# Check all services at once
./check-status.sh

# Check ports
./port-utils.sh check          # Check all development ports
./port-utils.sh backend        # Check backend (8080)
./port-utils.sh frontend       # Check frontend (5173)
```

## 🔧 Port Management

```bash
# Kill stuck processes
./port-utils.sh kill 8080      # Kill backend
./port-utils.sh kill 5173      # Kill frontend

# Manual check
lsof -i :8080                  # Check port 8080
lsof -i :5173                  # Check port 5173
```

## 🗄️ Database Management

```bash
# Quick commands
./db-utils.sh connect          # Connect to database
./db-utils.sh tables           # List tables
./db-utils.sh indexes          # List indexes
./db-utils.sh info             # Complete database info

# Detailed commands
./db-utils.sh describe alerts  # Describe table structure
./db-utils.sh count alerts     # Count rows in table
./db-utils.sh query "SELECT * FROM alerts LIMIT 5;"

# Service management
./db-utils.sh status           # Check PostgreSQL status
./db-utils.sh restart          # Restart PostgreSQL
./db-utils.sh backup           # Create backup
```

## 📡 API Testing

```bash
# Backend is running
curl http://localhost:8080/actuator/health

# Test with authentication
curl -u admin:admin123 http://localhost:8080/api/alerts

# Import Postman collection
# File: backend/Alerts-API-Postman-Collection.json
```

## 🐛 Common Issues

### "Port 8080 already in use"
```bash
./stop-backend.sh
# or
./port-utils.sh kill 8080
```

### "Port 5173 already in use"
```bash
./stop-frontend.sh
# or
./port-utils.sh kill 5173
```

### Database not accessible
```bash
brew services restart postgresql@16
./db-utils.sh status
```

### Check everything
```bash
./check-status.sh
./port-utils.sh check
```

## 📝 Development Workflow

### Starting your day
```bash
cd soc-copilot
./check-status.sh              # Check what's running
./port-utils.sh check          # Verify ports are free
./start-dev.sh                 # Start everything
```

### Ending your day
```bash
./stop-all.sh                  # Stop all services cleanly
```

### Quick restart
```bash
./stop-backend.sh && ./start-backend.sh
./stop-frontend.sh && ./start-frontend.sh
```

## 🔗 URLs

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api/alerts
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## 📚 Documentation

- Full setup guide: `DEV_SETUP.md`
- API documentation: `API_DOCUMENTATION.md`
- Postman guide: `backend/POSTMAN_GUIDE.md`
- Database guide: Use `./db-utils.sh` (no args for help)
- Port management: Use `./port-utils.sh` (no args for help)

## 🎯 Best Practices

1. **Always use stop scripts** instead of Ctrl+C
2. **Check ports before starting** with `./port-utils.sh check`
3. **Use check-status.sh** to see what's running
4. **Stop services cleanly** at end of day with `./stop-all.sh`
5. **Check logs** if issues occur: `backend.log` and `frontend.log`

## ⚡ One-Liners

```bash
# Full restart
./stop-all.sh && ./start-dev.sh

# Clean slate (stop everything and check)
./stop-all.sh && ./port-utils.sh check && ./check-status.sh

# Emergency cleanup (force kill everything)
./port-utils.sh kill 8080 && ./port-utils.sh kill 5173

# Quick database check
./db-utils.sh info && ./db-utils.sh tables

# Test API availability
curl -s http://localhost:8080/actuator/health | grep UP && echo "✓ API is UP"
```
