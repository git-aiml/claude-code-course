# Development Scripts Overview

## 🎯 TL;DR - What You Need to Know

**PostgreSQL Database: ALWAYS RUNNING** ✅ (recommended)
- Starts automatically when your Mac boots
- No need to manage it manually
- Uses minimal resources when idle

**Backend & Frontend: START/STOP AS NEEDED** 🔄
- Use scripts to start when you're developing
- Stop when you're done coding
- These restart frequently during development

## 📊 Service Management Matrix

| Service | How It Runs | When to Start | When to Stop | Scripts |
|---------|------------|---------------|--------------|---------|
| **PostgreSQL** | Background service | Auto on Mac boot | Rarely (troubleshooting) | `./db-utils.sh` |
| **Backend** | On-demand | When developing | End of session | `./start-backend.sh` / `./stop-backend.sh` |
| **Frontend** | On-demand | When developing | End of session | `./start-frontend.sh` / `./stop-frontend.sh` |

## 🚀 Your Daily Workflow

### Starting Your Day
```bash
cd soc-copilot

# 1. Check what's running (optional)
./check-status.sh

# 2. Start your application (PostgreSQL already running)
./start-dev.sh
```

### During Development
```bash
# Backend crashed? Restart it
./stop-backend.sh
./start-backend.sh

# PostgreSQL? Do nothing - it's running fine
```

### Ending Your Day
```bash
# Stop your applications
./stop-all.sh

# PostgreSQL? Leave it running - it's fine
```

## 📜 Complete Script Reference

### Application Management (Use These Daily)
```bash
./start-dev.sh          # Start backend + frontend
./stop-all.sh           # Stop backend + frontend
./start-backend.sh      # Start backend only
./stop-backend.sh       # Stop backend only
./start-frontend.sh     # Start frontend only
./stop-frontend.sh      # Stop frontend only
```

### Status Checking (Use Frequently)
```bash
./check-status.sh       # Check all services (backend, frontend, DB)
./port-utils.sh check   # Check all ports (8080, 5173, 5432)
./port-utils.sh backend # Check backend specifically
./port-utils.sh frontend # Check frontend specifically
```

### Database Management (Use Occasionally)
```bash
./db-utils.sh           # Show all database commands
./db-utils.sh status    # Check PostgreSQL status
./db-utils.sh connect   # Connect to database
./db-utils.sh tables    # List tables
./db-utils.sh info      # Complete database info
./db-utils.sh restart   # Restart PostgreSQL (troubleshooting)
```

### Port Management (Use When Issues)
```bash
./port-utils.sh kill 8080  # Kill stuck backend process
./port-utils.sh kill 5173  # Kill stuck frontend process
```

### Advanced (Rarely Needed)
```bash
./start-all-with-db.sh  # Start EVERYTHING including PostgreSQL (not recommended)
./stop-all-with-db.sh   # Stop EVERYTHING including PostgreSQL (not recommended)
```

## 🎓 Best Practices

### ✅ DO
- Use `./stop-backend.sh` instead of Ctrl+C
- Leave PostgreSQL running all the time
- Check status before starting: `./check-status.sh`
- Use `./port-utils.sh kill 8080` if port is stuck

### ❌ DON'T
- Don't use Ctrl+C to stop backend (leaves process running)
- Don't stop PostgreSQL unless troubleshooting
- Don't manually kill processes without checking first
- Don't start/stop PostgreSQL with every coding session

## 🆘 Quick Troubleshooting

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

### "Database connection refused"
```bash
./db-utils.sh status
# If stopped:
brew services start postgresql@16
```

### "Nothing works!"
```bash
./stop-all.sh
./port-utils.sh check
./check-status.sh
./start-dev.sh
```

## 📚 Detailed Documentation

- **DATABASE_MANAGEMENT.md** - Why PostgreSQL stays running
- **QUICK_REFERENCE.md** - One-page command cheat sheet
- **DEV_SETUP.md** - Full setup and troubleshooting guide
- **API_DOCUMENTATION.md** - REST API reference
- **POSTMAN_GUIDE.md** - API testing with Postman

## 🔗 Access URLs

- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api/alerts
- Health: http://localhost:8080/actuator/health
- Database: localhost:5432 (use `./db-utils.sh connect`)

## 💡 Remember

**3 Services, 3 Different Approaches:**

1. **PostgreSQL (5432)**: Background service - always running ✅
2. **Backend (8080)**: Application server - start/stop with scripts 🔄
3. **Frontend (5173)**: Dev server - start/stop with scripts 🔄

This is the industry-standard approach and matches production environments!
