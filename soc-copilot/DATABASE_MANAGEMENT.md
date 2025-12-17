# Database Management Guide

## Current Setup (Recommended)

PostgreSQL runs as a **background service** that starts automatically with your Mac and stays running.

## ✅ Recommended Approach: Database Always Running

### Why This is Best

| Aspect | Always Running (Recommended) | Start/Stop with App |
|--------|----------------------------|-------------------|
| **Startup Speed** | Instant - DB already running | +3-5 seconds wait for DB |
| **Data Persistence** | Data always available | Same |
| **Resource Usage** | ~50-100MB RAM (idle) | Same when running |
| **Risk** | Very low | More restarts = more risk |
| **Convenience** | High - forget about it | Must remember to start |
| **Multi-Project** | Works with all projects | Conflicts possible |
| **Production-like** | Yes - DBs run 24/7 | No |

### How It Works Now

```bash
# Database (PostgreSQL)
brew services start postgresql@16  # Already running!
# Starts automatically on Mac boot
# Stays running all the time

# Your Applications
./start-backend.sh    # Start when you need
./stop-backend.sh     # Stop when done

./start-frontend.sh   # Start when you need
./stop-frontend.sh    # Stop when done
```

### Benefits

1. **No Extra Steps** - Just start your app, DB is ready
2. **Faster Development** - No DB startup delay
3. **Multiple Projects** - One PostgreSQL serves all projects
4. **Production-like** - Mirrors real deployment
5. **Data Preserved** - Your test data survives restarts

## 🔧 Database Service Commands

### Check Status
```bash
./db-utils.sh status
# or
brew services list | grep postgres
```

### Restart (if issues)
```bash
./db-utils.sh restart
# or
brew services restart postgresql@16
```

### Stop Temporarily (rare)
```bash
brew services stop postgresql@16
```

### Start Again
```bash
brew services start postgresql@16
```

## 🤔 Alternative: Managing DB with Application

If you prefer to start/stop PostgreSQL with your application (NOT recommended), use these scripts:

### Start Everything Including DB
```bash
./start-all-with-db.sh
```
This will:
1. Start PostgreSQL service
2. Wait for it to be ready
3. Start backend and frontend

### Stop Everything Including DB
```bash
./stop-all-with-db.sh
```
This will:
1. Stop backend and frontend
2. Stop PostgreSQL service
3. Show warning that DB is stopped

### ⚠️ Downsides of This Approach

1. **Slower startup** - Wait 3-5 seconds for PostgreSQL each time
2. **Data at risk** - More start/stop cycles
3. **Inconvenient** - Must remember to start DB
4. **Multi-project issues** - Stops DB for other projects too
5. **Not production-like** - DBs don't restart in production

## 📊 Resource Usage Comparison

### PostgreSQL Running Idle
```
Memory: 50-100 MB (less than one browser tab)
CPU: <1% (essentially zero)
Disk I/O: Minimal (only when accessed)
```

### PostgreSQL During Development
```
Memory: 100-200 MB (with active connections)
CPU: 1-5% (during queries)
Disk I/O: Moderate (during data writes)
```

**Verdict:** PostgreSQL uses minimal resources when idle. No reason to stop it.

## 🎯 Recommended Workflow

### Daily Development
```bash
# Morning (or whenever you start coding)
cd soc-copilot
./check-status.sh              # Check what's running
./start-dev.sh                 # Start frontend & backend
# PostgreSQL already running - nothing needed!

# During development
./stop-backend.sh              # Stop/restart as needed
./start-backend.sh             # PostgreSQL stays running

# Evening (or when done)
./stop-all.sh                  # Stop frontend & backend
# Leave PostgreSQL running - no need to stop
```

### When to Manage PostgreSQL

**Restart PostgreSQL (Troubleshooting):**
```bash
./db-utils.sh restart
```

**Stop PostgreSQL (Freeing resources):**
```bash
brew services stop postgresql@16
# Your apps won't work until you start it again!
```

**Start PostgreSQL Again:**
```bash
brew services start postgresql@16
```

## 🔍 Checking Database Status

### Is PostgreSQL Running?
```bash
./db-utils.sh status
```

### Is Database Accessible?
```bash
./db-utils.sh connect
# Should connect immediately if PostgreSQL is running
```

### What's on Port 5432?
```bash
./port-utils.sh check
# or
lsof -i :5432
```

### Database Health
```bash
# Check PostgreSQL is ready for connections
/opt/homebrew/opt/postgresql@16/bin/pg_isready
```

## 📝 Script Summary

### Standard Workflow (Recommended)
```bash
./start-dev.sh              # Start frontend & backend
./stop-all.sh               # Stop frontend & backend
./check-status.sh           # Check all services
./db-utils.sh status        # Check PostgreSQL separately
```

### Alternative Workflow (Not Recommended)
```bash
./start-all-with-db.sh      # Start EVERYTHING including PostgreSQL
./stop-all-with-db.sh       # Stop EVERYTHING including PostgreSQL
```

## 🚨 Important Notes

### PostgreSQL Service vs Connection
- **Service Running**: PostgreSQL process is active (managed by Homebrew)
- **Connection Active**: Your app is connected to PostgreSQL (managed by Spring Boot)

When you stop your backend:
- ✅ Connection closes (Spring Boot disconnects)
- ✅ PostgreSQL keeps running (service stays up)
- ✅ Data is safe and available

### Database Files Location
```bash
# PostgreSQL data directory (your actual data)
/opt/homebrew/var/postgresql@16/

# This data persists whether PostgreSQL is running or not
```

## 🎓 Best Practices

1. **Leave PostgreSQL running** - It's designed to run continuously
2. **Use `./stop-backend.sh`** - For your application only
3. **Only restart PostgreSQL** - When troubleshooting DB issues
4. **Check status first** - Use `./check-status.sh` before starting
5. **Backup regularly** - Use `./db-utils.sh backup` for important data

## ❓ Common Questions

**Q: Does PostgreSQL use battery on my laptop?**
A: Minimal impact. When idle, it uses negligible CPU/battery.

**Q: What if I shut down my Mac?**
A: PostgreSQL stops automatically and starts when you boot again.

**Q: Can I have multiple databases on one PostgreSQL?**
A: Yes! You have `aisoc_copilot` now, but can create more:
```bash
/opt/homebrew/opt/postgresql@16/bin/createdb another_database
```

**Q: Will stopping PostgreSQL save significant resources?**
A: No. Idle PostgreSQL uses ~50-100MB RAM. Your browser uses much more.

**Q: What if I want complete control?**
A: Use `./start-all-with-db.sh` and `./stop-all-with-db.sh`, but understand the tradeoffs.

## 🔗 Related Documentation

- **QUICK_REFERENCE.md** - Quick command reference
- **DEV_SETUP.md** - Full setup documentation
- **db-utils.sh** - Database utility script (run without args for help)
