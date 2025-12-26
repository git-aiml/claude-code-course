# radioawa - Quick Start Guide

The fastest way to start, stop, and access radioawa streaming player.

## Prerequisites

Ensure you have installed:
- Java 17+
- Maven 3.6+
- Node.js 18+
- PostgreSQL 16 (via Homebrew)

---

## ⚙️ Two Deployment Modes

RadioAWA supports two deployment modes with different purposes:

### 🛠️ Development Mode (Local Development)

**Purpose:** Daily development work with fast iteration

**Features:**
- ✅ **Hot Reload**: Code changes appear instantly
- ✅ **Vite Dev Server**: Frontend on port 5171
- ✅ **Debug Logging**: Verbose output for troubleshooting
- ✅ **Source Maps**: Easy debugging in browser
- ✅ **All Ports Exposed**: Easy API testing

**Start Command:**
```bash
# Docker method
docker compose up

# OR Manual method (this guide)
./start-all.sh
```

**Access:** http://localhost:5171

---

### 🚀 Production Mode (Deployment)

**Purpose:** Optimized deployment to servers

**Features:**
- ✅ **Nginx Web Server**: Serves optimized static files on port 80
- ✅ **Minified Code**: 90% smaller than development
- ✅ **Security Headers**: X-Frame-Options, CSP, etc.
- ✅ **Gzip Compression**: Faster page loads
- ✅ **API Reverse Proxy**: Backend hidden behind Nginx
- ✅ **Immutable Containers**: No code changes without rebuild

**Start Command:**
```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

**Access:** http://localhost (port 80)

---

### 📋 Key Differences Summary

| Aspect | Development Mode | Production Mode |
|--------|------------------|-----------------|
| **Frontend Server** | Vite (port 5171) | Nginx (port 80) |
| **Config File** | `vite.config.js` | `nginx.conf` |
| **Hot Reload** | ✅ Yes | ❌ No |
| **Image Size** | ~400MB | ~40MB |
| **Startup Time** | 20-30s | ~10s |
| **Security** | Relaxed | Hardened |

### 🎯 Which Mode to Use?

**Use Development Mode when:**
- Coding and testing locally
- Making frequent code changes
- Debugging issues
- Following this quickstart guide

**Use Production Mode when:**
- Deploying to a server
- Testing production build locally
- Performance testing
- See [DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md) for details

---

## 🔧 First-Time Setup (Required)

**If this is your first time running radioawa locally**, you need to initialize the database:

```bash
./setup-local-db.sh
```

This script will:
- ✅ Create the `radioawa` database user
- ✅ Create the `radioawa` database
- ✅ Run database migrations (create tables, indexes)
- ✅ Insert initial station data (English & Hindi stations)
- ✅ Verify the setup

**You only need to run this once!** After setup, use `./start-all.sh` to start the application.

### When to Run Setup Script

Run `./setup-local-db.sh` if:
- This is your first time running radioawa locally
- You get database connection errors
- You want to reset the database to a fresh state
- You've deleted the radioawa database

**Note:** Docker users don't need this - the database is auto-configured in containers.

---

## 🚀 Starting radioawa

### Option 1: Using Scripts (Recommended - Easiest!)

We have automated scripts to make your life easier:

```bash
./start-all.sh
```

That's it! The script will:
- ✅ Start PostgreSQL database
- ✅ Start Spring Boot backend (port 8081)
- ✅ Start React frontend (port 5171)
- ✅ Show you all the URLs

**Access the player:** Open `http://localhost:5171` in your browser

---

### Option 2: Manual Start (If you prefer control)

Open **3 separate terminal windows**:

#### Terminal 1: Start Backend
```bash
cd backend
mvn spring-boot:run
```
✅ Backend running on: **http://localhost:8081**

#### Terminal 2: Start Frontend
```bash
cd frontend
npm install  # Only needed first time
npm run dev
```
✅ Frontend running on: **http://localhost:5171**

#### Terminal 3: Ensure PostgreSQL is Running
```bash
brew services start postgresql@16
```
✅ PostgreSQL running on: **localhost:5432**

---

## Accessing radioawa

### Step 1: Open Browser
Navigate to:
```
http://localhost:5171
```

### Step 2: Select a Station
1. You'll see two station options at the top: **English** and **हिंदी** (Hindi)
2. Click either button to switch stations
3. Each station has its own stream and song library

### Step 3: Start Streaming
1. You'll see the **radioawa player** with an animated logo
2. Click the large **Play button** (circular button in center)
3. Status will change from "Ready to Play" to **"LIVE"** (green)
4. Audio will start playing

### Step 4: Control Playback & Rate Songs
- **Play/Pause**: Click the center button
- **Volume**: Adjust the slider (0-100%)
- **Status**: Watch the status indicator (Offline/Ready/Loading/LIVE/Error)
- **Rate Songs**: Use thumbs up/down buttons to rate the currently playing song
- **Switch Stations**: Click English or हिंदी buttons at the top

## Application URLs

| Component | URL | Purpose |
|-----------|-----|---------|
| **Frontend** | http://localhost:5171 | Radio player interface |
| **Backend** | http://localhost:8081 | Spring Boot API |
| **Health Check** | http://localhost:8081/api/health | Backend status |
| **Database** | localhost:5432 | PostgreSQL (if running) |

## Verifying Everything is Running

### Check Backend
```bash
curl http://localhost:8081/api/health
```
**Expected response:**
```json
{
  "status": "UP",
  "service": "Radioawa Backend"
}
```

### Check Frontend
Open browser to `http://localhost:5171` - you should see the radioawa interface.

### Check Database
```bash
brew services list | grep postgresql
```
**Expected output:**
```
postgresql@16  started
```

### Check All Processes
```bash
# Backend (Java process on port 8081)
lsof -i :8081

# Frontend (Vite dev server on port 5171)
lsof -i :5171

# Database (PostgreSQL on port 5432)
lsof -i :5432
```

## 🛑 Stopping radioawa

### Option 1: Using Scripts (Recommended)

```bash
./stop-all.sh
```

This stops backend and frontend. PostgreSQL stays running (managed by Homebrew).

---

### Option 2: Manual Stop

**Stop Frontend & Backend:**
- Press `Ctrl + C` in each terminal

**Stop Database:**
```bash
brew services stop postgresql@16
```

---

## 📊 Checking Status

Want to see what's running?

```bash
./check-status.sh
```

This shows:
- ✅ PostgreSQL status
- ✅ Backend status (with API check)
- ✅ Frontend status
- ✅ Quick commands reference

## Common Issues & Solutions

### Issue: Backend won't start
**Cause:** Port 8081 is already in use
**Solution:**
```bash
# Find what's using port 8081
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or change port in backend/src/main/resources/application.properties
```

### Issue: Frontend won't start
**Cause:** Port 5171 is already in use
**Solution:**
```bash
# Find what's using port 5171
lsof -i :5171

# Kill the process
kill -9 <PID>

# Or change port in frontend/vite.config.js
```

### Issue: "Backend is down" message
**Cause:** Backend not running or not accessible
**Solution:**
1. Check backend terminal for errors
2. Verify backend is running: `curl http://localhost:8081/api/health`
3. Restart backend: `cd backend && mvn spring-boot:run`

### Issue: Stream won't play
**Cause:** Stream URL not accessible or browser issue
**Solution:**
1. Click play button again
2. Check browser console (F12) for errors
3. Try a different browser (Safari has native HLS support)
4. Verify stream URL is accessible
5. Check your internet connection

### Issue: Database connection error
**Cause:** PostgreSQL not running
**Solution:**
```bash
# Start database
brew services start postgresql@16

# Check status
brew services list | grep postgresql

# View logs
brew services info postgresql@16
```

## Stream Information

### English Station
**Stream URL:** `https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8`

**Quality:** 24-bit / 48 kHz Lossless HLS

**Format:** AAC (Advanced Audio Coding)

### Hindi Station (Vividh Bharati)
**Stream URL:** `https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8`

**Content:** Classic Hindi film music from All India Radio

**Quality:** High-quality HLS stream

**Compatibility:** All modern browsers (Chrome, Firefox, Safari, Edge)

## Development Workflow

### Typical Development Session

1. **Start everything:**
   ```bash
   # Terminal 1
   cd backend && mvn spring-boot:run

   # Terminal 2
   cd frontend && npm run dev

   # Terminal 3 (ensure PostgreSQL is running)
   brew services start postgresql@16
   ```

2. **Develop:**
   - Frontend changes: Auto-reload (HMR)
   - Backend changes: Auto-restart (Spring DevTools)
   - Both terminals stay open

3. **Test:**
   - Open http://localhost:5171
   - Test player functionality
   - Check browser console for errors

4. **Stop everything:**
   ```bash
   # Ctrl+C in terminal 1 (backend)
   # Ctrl+C in terminal 2 (frontend)
   # brew services stop postgresql@16 (if needed)
   ```

## Quick Reference Commands

### Start
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm run dev

# Database (ensure running)
brew services start postgresql@16
```

### Stop
```bash
# Backend & Frontend: Ctrl+C

# Database (if needed)
brew services stop postgresql@16
```

### Access
```bash
# Frontend
http://localhost:5171

# Backend Health
http://localhost:8081/api/health
```

### Logs
```bash
# Backend: Check terminal output

# Frontend: Check terminal output

# Database
brew services info postgresql@16
```

### Clean Start
```bash
# Backend (clean build)
cd backend
mvn clean install
mvn spring-boot:run

# Frontend (fresh install)
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev

# Database (reset database)
brew services restart postgresql@16
```

### Testing
```bash
# Run backend tests
cd backend
mvn test

# Run frontend tests
cd frontend
npm run test

# Generate coverage reports
mvn jacoco:report          # Backend coverage
npm run test:coverage      # Frontend coverage
```

## Status Indicators

| Indicator | Color | Meaning |
|-----------|-------|---------|
| **Offline** | Gray | Player not initialized |
| **Ready to Play** | Mint/Gray | Player loaded, ready to stream |
| **Loading...** | Orange | Buffering stream |
| **LIVE** | Green (pulsing) | Actively streaming |
| **Error** | Red | Stream or connection error |

## Browser Support

| Browser | Support | Notes |
|---------|---------|-------|
| **Chrome** | ✅ Full | Uses HLS.js |
| **Firefox** | ✅ Full | Uses HLS.js |
| **Safari** | ✅ Full | Native HLS support |
| **Edge** | ✅ Full | Uses HLS.js |

## Next Steps

- **Customize**: See [README.md](./README.md) for customization options
- **Deploy**: See [DEPLOYMENT.md](./DEPLOYMENT.md) for production deployment
- **Setup Details**: See [SETUP.md](./SETUP.md) for detailed setup instructions

---

---

## 📝 Complete Scripts Reference

| Script | Purpose |
|--------|---------|
| `./setup-local-db.sh` | **First-time setup**: Initialize database (run once) |
| `./start-all.sh` | Start all services (backend, frontend, PostgreSQL) |
| `./stop-all.sh` | Stop backend and frontend |
| `./check-status.sh` | Check status of all services |

---

## 🎯 Quick Summary

### Easiest Way (Using Scripts):

**First-time setup (run once):**
```bash
./setup-local-db.sh
```

**Start:**
```bash
./start-all.sh
```

**Access:**
```
http://localhost:5171
```

**Stop:**
```bash
./stop-all.sh  # or ./stop-all.sh
```

**Check:**
```bash
./check-status.sh
```

### Manual Way:

**Terminal 1:** `cd backend && mvn spring-boot:run`
**Terminal 2:** `cd frontend && npm run dev`
**Terminal 3:** `brew services start postgresql@16` (if needed)
**Stop:** `Ctrl+C` in each terminal

---

That's it! Enjoy your radioawa lossless streaming! 🎵
