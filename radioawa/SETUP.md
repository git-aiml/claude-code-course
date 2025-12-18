# radioawa - Detailed Setup Guide

This guide provides step-by-step instructions for setting up the radioawa streaming platform on your local machine.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [System Requirements](#system-requirements)
3. [Installation Steps](#installation-steps)
4. [Configuration](#configuration)
5. [Running the Application](#running-the-application)
6. [Verification](#verification)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software

Before you begin, ensure you have the following installed:

#### 1. Java Development Kit (JDK) 17+

**Check if installed:**
```bash
java -version
```

**Expected output:**
```
java version "17.0.x" or higher
```

**Installation:**
- **macOS**: `brew install openjdk@17`
- **Ubuntu/Debian**: `sudo apt install openjdk-17-jdk`
- **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)

#### 2. Maven 3.6+

**Check if installed:**
```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.6.x or higher
```

**Installation:**
- **macOS**: `brew install maven`
- **Ubuntu/Debian**: `sudo apt install maven`
- **Windows**: Download from [Apache Maven](https://maven.apache.org/download.cgi)

#### 3. Node.js 18+ and npm

**Check if installed:**
```bash
node -v
npm -v
```

**Expected output:**
```
v18.x.x or higher
9.x.x or higher
```

**Installation:**
- **macOS**: `brew install node`
- **Ubuntu/Debian**: `sudo apt install nodejs npm`
- **Windows**: Download from [nodejs.org](https://nodejs.org/)
- **All platforms**: Use [nvm](https://github.com/nvm-sh/nvm) for version management

#### 4. Docker and Docker Compose (Optional)

Required only if you want to use PostgreSQL database features.

**Check if installed:**
```bash
docker --version
docker compose version
```

**Installation:**
- Download from [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## System Requirements

### Minimum Requirements

- **CPU**: 2 cores
- **RAM**: 4 GB
- **Disk Space**: 2 GB free
- **Internet**: Broadband connection for streaming

### Recommended Requirements

- **CPU**: 4+ cores
- **RAM**: 8+ GB
- **Disk Space**: 5+ GB free
- **Internet**: High-speed broadband (10+ Mbps)

## Installation Steps

### Step 1: Clone or Download the Project

If you have the project as a ZIP file:
```bash
cd /path/to/your/projects
unzip radioawa.zip
cd radioawa
```

If using Git:
```bash
git clone <repository-url>
cd radioawa
```

### Step 2: Set Up the Backend

Navigate to the backend directory:
```bash
cd backend
```

#### Build the Backend

```bash
mvn clean install
```

This command will:
- Download all required dependencies
- Compile the Java code
- Run tests (if any)
- Create a JAR file in the `target/` directory

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: xx.xxx s
```

#### Verify Backend Configuration

Check the configuration file:
```bash
cat src/main/resources/application.properties
```

Key settings to verify:
- Server port: `8081`
- Database connection (if using PostgreSQL)
- CORS settings

### Step 3: Set Up the Frontend

Open a new terminal and navigate to the frontend directory:
```bash
cd frontend
```

#### Install Dependencies

```bash
npm install
```

This will install:
- React 19
- Vite 7
- HLS.js
- All other required packages

**Expected output:**
```
added XXX packages in XXs
```

#### Verify Package Installation

Check that HLS.js was installed:
```bash
npm list hls.js
```

**Expected output:**
```
radioawa-frontend@0.0.0 /path/to/radioawa/frontend
└── hls.js@x.x.x
```

### Step 4: Set Up Database (Optional)

If you want to use database features:

#### Start PostgreSQL with Docker

```bash
# From the project root
docker compose up -d
```

#### Verify Database is Running

```bash
docker compose ps
```

**Expected output:**
```
NAME                IMAGE            STATUS
radioawa-postgres   postgres:16      Up
```

#### Connect to Database (Optional)

```bash
docker exec -it radioawa-postgres psql -U radioawa -d radioawa
```

Password: `radioawa_dev_password`

To exit: `\q`

## Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8081

# Database Configuration (if using PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/radioawa
spring.datasource.username=radioawa
spring.datasource.password=radioawa_dev_password

# CORS Configuration
cors.allowed-origins=http://localhost:5171
```

### Frontend Configuration

The default configuration works out of the box. To customize:

#### Change Development Server Port

Edit `frontend/vite.config.js`:

```javascript
export default defineConfig({
  server: {
    port: 5171, // Change this to your preferred port
    proxy: {
      '/api': 'http://localhost:8081'
    }
  }
})
```

#### Change Stream URL

Edit `frontend/src/components/RadioPlayer.jsx`:

```javascript
const streamUrl = 'https://your-stream-url.com/path/to/stream.m3u8'
```

## Running the Application

### Method 1: Run Both Servers Separately

This is the recommended method for development.

#### Terminal 1: Start Backend

```bash
cd backend
mvn spring-boot:run
```

**Expected output:**
```
Started RadioawaApplication in X.XXX seconds
```

The backend is now running at: `http://localhost:8081`

#### Terminal 2: Start Frontend

```bash
cd frontend
npm run dev
```

**Expected output:**
```
VITE vX.X.X  ready in XXX ms

➜  Local:   http://localhost:5171/
➜  Network: use --host to expose
```

The frontend is now running at: `http://localhost:5171`

### Method 2: Run Backend in Background

```bash
# Start backend
cd backend
mvn spring-boot:run &

# Wait a few seconds for backend to start
sleep 10

# Start frontend
cd ../frontend
npm run dev
```

### Method 3: Using Docker Compose (Advanced)

If you've configured Docker Compose for the entire stack:

```bash
docker compose up
```

## Verification

### Step 1: Verify Backend is Running

Open a browser or use curl:
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

### Step 2: Access the Radio Player

Open your browser and navigate to:
```
http://localhost:5171
```

You should see:
- ✅ radioawa logo with animated radio waves
- ✅ "RADIO AWA" heading
- ✅ "24-BIT LOSSLESS STREAM" subtitle
- ✅ Play button (large circular button)
- ✅ Volume slider
- ✅ Status indicator showing "Ready to Play"
- ✅ Backend status showing "Backend connected: Radioawa Backend"

### Step 3: Test the Stream

1. Click the **Play button**
2. Status should change to **"LIVE"** with green background
3. You should hear audio playing
4. Try adjusting the volume slider
5. Click Play button again to pause

### Step 4: Check Browser Console

Press `F12` to open Developer Tools, then check the Console tab.

**You should see:**
```
HLS manifest loaded
```

**You should NOT see:**
- Red error messages
- CORS errors
- Network errors

## Troubleshooting

### Backend Issues

#### Issue: Port 8081 already in use

**Solution:**
```bash
# Find what's using port 8081
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or change the port in application.properties
```

#### Issue: Maven build fails

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

#### Issue: Backend won't start - Database connection error

**Solution:**
```bash
# Make sure PostgreSQL is running
docker compose ps

# Or disable database in application.properties
# Comment out database-related properties
```

### Frontend Issues

#### Issue: Port 5171 already in use

**Solution:**
```bash
# Find what's using port 5171
lsof -i :5171

# Kill the process
kill -9 <PID>

# Or change the port in vite.config.js
```

#### Issue: npm install fails

**Solution:**
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

#### Issue: Module not found errors

**Solution:**
```bash
# Ensure you're in the frontend directory
cd frontend

# Install dependencies again
npm install

# If issue persists, check Node.js version
node -v  # Should be 18+
```

### Stream Issues

#### Issue: Stream won't play

**Checklist:**
1. ✅ Open the stream URL directly in browser: `https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8`
2. ✅ Check browser console for errors (F12)
3. ✅ Try a different browser (Safari has native HLS support)
4. ✅ Check your internet connection
5. ✅ Verify the stream is currently live and broadcasting

#### Issue: "HLS is not supported in your browser"

**Solution:**
- Update your browser to the latest version
- Try Safari (has native HLS support)
- Check that JavaScript is enabled

#### Issue: Audio cuts out or buffers frequently

**Solution:**
- Check your internet speed (should be 5+ Mbps)
- Close other streaming applications
- Move closer to WiFi router
- Try lowering stream quality settings

### Database Issues

#### Issue: Can't connect to PostgreSQL

**Solution:**
```bash
# Check if container is running
docker compose ps

# Check container logs
docker compose logs postgres

# Restart database
docker compose restart postgres
```

#### Issue: Permission denied on PostgreSQL

**Solution:**
```bash
# Reset database with fresh volumes
docker compose down -v
docker compose up -d
```

## Next Steps

After successful setup:

1. **Explore the Code**
   - Frontend: `frontend/src/components/RadioPlayer.jsx`
   - Backend: `backend/src/main/java/com/radioawa/`

2. **Customize the Stream**
   - Change stream URL in `RadioPlayer.jsx`
   - Test with different HLS streams

3. **Modify Branding**
   - Update colors in `frontend/src/index.css`
   - Modify logo in `RadioPlayer.jsx`

4. **Add Features**
   - Implement playlist management
   - Add track metadata display
   - Create admin panel

5. **Deploy to Production**
   - See [DEPLOYMENT.md](./DEPLOYMENT.md) for production deployment guide

## Getting Help

If you encounter issues not covered here:

1. Check the browser console (F12) for error messages
2. Check backend logs in the terminal
3. Review the main [README.md](./README.md)
4. Check the [DEPLOYMENT.md](./DEPLOYMENT.md) for production-specific issues

## Summary of Commands

### Quick Start Commands

```bash
# Terminal 1: Backend
cd backend
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
npm install
npm run dev

# Terminal 3: Database (optional)
docker compose up -d

# Access application
# Open browser: http://localhost:5171
```

### Stop Commands

```bash
# Stop frontend: Ctrl+C in terminal
# Stop backend: Ctrl+C in terminal
# Stop database: docker compose down
```

That's it! You should now have radioawa running on your local machine. Enjoy streaming!
