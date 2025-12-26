# RadioAwa Docker Deployment Guide

## Table of Contents
- [Overview](#overview)
- [Why Docker for RadioAwa?](#why-docker-for-radioawa)
- [Prerequisites](#prerequisites)
- [Architecture](#architecture)
- [How Docker Works for RadioAwa](#how-docker-works-for-radioawa)
- [Development Deployment](#development-deployment)
- [Production Deployment](#production-deployment)
- [Docker Commands Reference](#docker-commands-reference)
- [Container Images Explained](#container-images-explained)
- [Networking & Communication](#networking--communication)
- [Data Persistence](#data-persistence)
- [Docker Best Practices for Beginners](#docker-best-practices-for-beginners)
- [Known Issues](#known-issues)
- [Troubleshooting](#troubleshooting)
- [Performance Tuning](#performance-tuning)
- [Security Best Practices](#security-best-practices)
- [Next Steps](#next-steps)

---

## Overview

### What is RadioAwa?

**RadioAwa** is a modern, crystal-clear lossless audio streaming platform featuring **24-bit / 48 kHz** high-fidelity audio streaming via HLS (HTTP Live Streaming). The application provides:

#### 🎵 **Two Radio Stations**

1. **RadioAwa English** 🎧
   - Stream URL: CloudFront CDN delivery
   - Content: English music, news, and entertainment
   - Audio Quality: 24-bit lossless
   - Station Code: `ENGLISH`

2. **RadioAwa Hindi** 🎶
   - Stream URL: Mux video streaming platform
   - Content: Hindi music, Bollywood hits, and cultural programming
   - Audio Quality: High-definition streaming
   - Station Code: `HINDI`

#### ✨ **Key Features for Users**

- **Dual-Station Selection**: Seamlessly switch between English and Hindi radio stations
- **High-Quality Audio**: Lossless 24-bit streaming for premium listening experience
- **Interactive Ratings**: Thumbs up/down voting system for songs
- **Now Playing Display**: Real-time song information with album artwork
- **Recently Played History**: Track your listening history
- **IP-Based Rate Limiting**: Fair usage with 20 votes/hour per station
- **Cross-Platform**: Works on Chrome, Firefox, Safari, and Edge browsers
- **Responsive Design**: Optimized for desktop and mobile devices

#### 🏗️ **Technology Stack**

- **Frontend**: React 19 + Vite 7 (development) / Nginx (production)
- **Backend**: Spring Boot 3.2.1 + Java 17
- **Database**: PostgreSQL 16 with automatic initialization
- **Streaming**: HLS.js for adaptive HTTP Live Streaming
- **Containerization**: Docker with multi-stage builds for dev and prod

### Why This Guide?

This comprehensive guide will help you:
- **Deploy RadioAwa** using Docker containers (development or production)
- **Understand** how Docker containerization works for this project
- **Learn** Docker best practices through practical examples
- **Troubleshoot** common issues when running containerized applications
- **Scale** the application for production use

Whether you're a beginner learning Docker or an experienced developer deploying to production, this guide provides step-by-step instructions with explanations.

---

## Why Docker for RadioAwa?

### Purpose of Containerization

Containerizing RadioAwa addresses several critical deployment and development challenges:

#### 1. **Environment Consistency**
- **Problem**: "It works on my machine" syndrome where applications behave differently across developer machines, testing environments, and production servers
- **Solution**: Docker ensures identical runtime environments across all stages of development and deployment
- **Impact**: Eliminates configuration drift and reduces deployment failures by 90%+

#### 2. **Simplified Dependency Management**
- **Problem**: RadioAwa requires Java 17, Node.js 20, PostgreSQL 16, and specific library versions
- **Solution**: All dependencies are packaged within containers, isolated from the host system
- **Impact**: New developers can start contributing in minutes instead of hours spent on environment setup

#### 3. **Microservices Architecture**
- **Frontend**: React + Vite development server (dev) or Nginx static server (prod)
- **Backend**: Spring Boot REST API with JPA/Hibernate
- **Database**: PostgreSQL with automatic schema initialization
- **Benefit**: Each service can be scaled, updated, and deployed independently

#### 4. **Development-Production Parity**
- **Problem**: Traditional deployments often have significant differences between dev and prod environments
- **Solution**: Multi-stage Docker builds provide optimized images for each environment while maintaining consistency
- **Impact**: Reduces production bugs caused by environment differences

#### 5. **Easy Deployment & Portability**
- **Cloud-Ready**: Deploy to AWS ECS, Google Cloud Run, Azure Container Instances, or any Kubernetes cluster
- **On-Premise**: Run on any server with Docker installed
- **Local Development**: Full application stack running with a single command
- **CI/CD Integration**: Automated builds and deployments with GitHub Actions, GitLab CI, or Jenkins

#### 6. **Resource Isolation & Security**
- Containers provide process isolation and resource limits
- Production containers run as non-root users
- Database and backend are not exposed externally in production
- Each service has minimal attack surface

### Key Benefits for RadioAwa

1. **Fast Onboarding**: New developers can run the entire stack with `docker compose up`
2. **Consistent Testing**: CI/CD pipelines use the same containers as production
3. **Easy Rollbacks**: Version-tagged images allow instant rollback to previous releases
4. **Scalability**: Horizontal scaling by running multiple container instances
5. **Cost Efficiency**: Optimized production images reduce cloud hosting costs
6. **Monitoring**: Built-in health checks for all services

---

## Prerequisites

### Required Software

- **Docker Engine 20.10+** ([Install Docker](https://docs.docker.com/get-docker/))
- **Docker Compose 2.0+** (included with Docker Desktop)
- **At least 4GB RAM** available for containers
- **At least 10GB disk space** for images and volumes

### Verify Installation

```bash
docker --version
# Expected: Docker version 20.10.x or higher

docker compose version
# Expected: Docker Compose version v2.x.x or higher
```

### Optional Tools

- **curl** - For testing API endpoints
- **jq** - For formatting JSON responses
- **psql** - For direct database access (if needed)

---

## Architecture

### System Components

The RadioAwa application consists of three main services:

```
┌─────────────────────────────────────────────────────────┐
│                    RadioAwa Stack                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌─────────────────┐     ┌──────────────────┐         │
│  │   Frontend      │────▶│   Backend        │         │
│  │   Container     │     │   Container      │         │
│  │                 │     │                  │         │
│  │ React + Vite    │     │ Spring Boot      │         │
│  │ (Dev: Port      │     │ REST API         │         │
│  │  5171)          │     │ Port 8081        │         │
│  │                 │     │                  │         │
│  │ Nginx           │     │ JPA/Hibernate    │         │
│  │ (Prod: Port 80) │     │ Actuator         │         │
│  └─────────────────┘     └──────────────────┘         │
│           │                       │                    │
│           │                       │                    │
│           │                       ▼                    │
│           │              ┌──────────────────┐         │
│           │              │   PostgreSQL     │         │
│           │              │   Container      │         │
│           │              │                  │         │
│           │              │ Database         │         │
│           │              │ Port 5432        │         │
│           │              │                  │         │
│           │              │ Stations         │         │
│           │              │ Songs            │         │
│           │              │ Ratings          │         │
│           │              └──────────────────┘         │
│           │                                            │
│           ▼                                            │
│      End Users                                         │
│   (Web Browsers)                                       │
└─────────────────────────────────────────────────────────┘
```

### Service Details

#### 1. **PostgreSQL Database** (postgres:16-alpine)
   - **Purpose**: Persistent data storage for stations, songs, and ratings
   - **Features**:
     - Health checks enabled
     - Automatic initialization with migration scripts
     - Data persisted in Docker volumes
   - **Initialized Data**:
     - 2 radio stations (English & Hindi)
     - Songs table with station associations
     - Ratings table with IP-based tracking
   - **Image Size**: ~230MB (Alpine Linux base)

#### 2. **Backend Service** (Spring Boot 3.2.1 + Java 17)
   - **Purpose**: REST API for stations, songs, and ratings management
   - **Endpoints**:
     - `GET /api/stations` - List all active radio stations
     - `GET /api/ratings/{stationCode}` - Get song ratings for a station
     - `POST /api/ratings` - Submit song ratings
     - `GET /actuator/health` - Health check (production only)
   - **Features**:
     - JPA/Hibernate for database operations
     - IP-based rate limiting (20 votes/hour per station)
     - CORS configuration for frontend
     - Actuator for health monitoring (prod only)
   - **Image Size**:
     - Development: ~800MB (includes Maven)
     - Production: ~250MB (JRE only, 70% smaller)

#### 3. **Frontend Service** (React 19 + Vite 7)
   - **Purpose**: User interface for streaming and interaction
   - **Features**:
     - **Development**: Vite dev server with hot reload (HMR)
     - **Production**: Nginx serving static files with API proxy
     - Station selector for English/Hindi switching
     - HLS.js player for audio streaming
     - Real-time song ratings and display
   - **Image Size**:
     - Development: ~400MB (Node.js + npm)
     - Production: ~40MB (Nginx only, 90% smaller)

### Network Communication

All services communicate through a dedicated Docker bridge network (`radioawa-network`), providing:
- **Service Discovery**: Services reference each other by name (e.g., `postgres`, `backend`)
- **Isolation**: Network traffic is isolated from the host and other Docker networks
- **Security**: Internal communication only; external access controlled via port mapping

---

## How Docker Works for RadioAwa

### Understanding the Docker Lifecycle

This section explains what happens behind the scenes when you run Docker commands, helping beginners understand the containerization process.

### What Happens During `docker compose up`

When you execute `docker compose up`, Docker performs the following steps in order:

#### Step 1: **Image Building** (if needed)
```bash
docker compose up --build
```

**What Docker Does:**
1. **Reads Dockerfiles**: Docker examines `backend/Dockerfile` and `frontend/Dockerfile`
2. **Multi-Stage Build Process**:
   - **Backend**:
     - Stage 1 (Build): Downloads Maven dependencies, compiles Java code, creates JAR file
     - Stage 2 (Development): Sets up Maven with source code mounting
     - Stage 3 (Production): Copies JAR to minimal JRE image
   - **Frontend**:
     - Stage 1 (Development): Installs npm packages, sets up Vite dev server
     - Stage 2 (Build): Compiles React code, bundles assets
     - Stage 3 (Production): Copies build output to Nginx image
3. **Layer Caching**: Docker caches each step; unchanged layers are reused (makes rebuilds fast)
4. **Tags Images**: Creates local images like `radioawa-backend:latest` and `radioawa-frontend:latest`

**Time Taken**: First build ~5-10 minutes, subsequent builds ~30 seconds (with cache)

#### Step 2: **Network Creation**
```
Creating network "radioawa_radioawa-network"
```

**What Docker Does:**
1. Creates a virtual network named `radioawa_radioawa-network`
2. Assigns IP address range (e.g., 172.18.0.0/16)
3. Enables DNS resolution (services can ping each other by name)
4. Isolates network from host and other Docker networks

**Result**: Services can communicate using names like `http://backend:8081` or `postgres:5432`

#### Step 3: **Volume Creation**
```
Creating volume "radioawa_postgres-data"
Creating volume "radioawa_maven-cache"
```

**What Docker Does:**
1. **postgres-data**: Creates persistent storage for PostgreSQL database files
   - Location (Mac): `~/Library/Containers/com.docker.docker/Data/vms/0/`
   - Location (Linux): `/var/lib/docker/volumes/radioawa_postgres-data/`
2. **maven-cache**: Creates storage for Maven dependencies (development only)
3. These volumes persist even when containers are deleted

**Result**: Database data survives container restarts and rebuilds

#### Step 4: **Container Creation**
```
Creating radioawa-postgres-dev
Creating radioawa-backend-dev
Creating radioawa-frontend-dev
```

**What Docker Does:**
1. **Creates Container Instances**: Each service gets a lightweight, isolated runtime environment
2. **Allocates Resources**: CPU, memory, and storage limits applied
3. **Mounts Volumes**:
   - Production: Named volumes for persistence
   - Development: Bind mounts (e.g., `./backend/src` → `/app/src` for hot reload)
4. **Configures Environment**: Injects environment variables from `.env` file
5. **Sets Up Networking**: Connects containers to `radioawa-network`

#### Step 5: **Container Startup** (Order Matters!)
```
Starting radioawa-postgres-dev
Waiting for postgres to be healthy...
Starting radioawa-backend-dev
Starting radioawa-frontend-dev
```

**Startup Order (defined by `depends_on`):**

**1. PostgreSQL starts first:**
   - Initializes database cluster
   - Runs `multi-station-migration.sql`:
     - Creates `stations` table
     - Inserts English and Hindi stations
     - Creates `songs` and `ratings` tables (if needed)
     - Sets up foreign keys and indexes
   - Becomes "healthy" (checked via `pg_isready`)
   - **Time**: ~10-15 seconds

**2. Backend waits for PostgreSQL, then starts:**
   - Waits for PostgreSQL health check to pass
   - Maven downloads dependencies (first run) or uses cache
   - Spring Boot application starts:
     - Connects to PostgreSQL
     - Runs JPA schema validation
     - Initializes REST API endpoints
     - Starts embedded Tomcat server on port 8081
   - **Time**: ~30-60 seconds (first run), ~20 seconds (subsequent)

**3. Frontend starts (doesn't depend on backend):**
   - Development: Vite dev server starts on port 5171
   - Production: Nginx starts serving static files on port 80
   - **Time**: ~5-10 seconds

#### Step 6: **Health Checks** (Continuous)

Docker continuously monitors service health:

```yaml
# PostgreSQL Health Check
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U radioawa"]
  interval: 10s      # Check every 10 seconds
  timeout: 5s        # Wait max 5 seconds for response
  retries: 5         # Retry 5 times before marking unhealthy
```

**What This Means:**
- Docker runs `pg_isready` inside the PostgreSQL container every 10 seconds
- If check fails 5 times in a row, container marked as "unhealthy"
- Dependent services won't start if health checks fail

**View Health Status:**
```bash
docker compose ps
# Shows: Up (healthy) or Up (unhealthy)
```

#### Step 7: **Port Mapping** (Host ↔ Container)

Docker maps container ports to your host machine:

```
Development:
  localhost:5432  → postgres:5432   (Database)
  localhost:8081  → backend:8081    (API)
  localhost:5171  → frontend:5171   (Web UI)

Production:
  localhost:80    → frontend:80     (Web UI only)
  # Backend and database NOT exposed (security)
```

**How It Works:**
- Docker creates a port forwarding rule in your OS
- Requests to `localhost:8081` are forwarded to the backend container
- Inside the container, Spring Boot listens on port 8081

---

### What Happens During `docker compose down`

When you execute `docker compose down`, Docker performs cleanup:

#### Step 1: **Graceful Shutdown**
```
Stopping radioawa-frontend-dev
Stopping radioawa-backend-dev
Stopping radioawa-postgres-dev
```

**What Docker Does:**
1. Sends `SIGTERM` signal to each container (requests polite shutdown)
2. Waits 10 seconds for graceful shutdown
3. If container doesn't stop, sends `SIGKILL` (forceful termination)

**Application Behavior:**
- **Spring Boot**: Finishes current requests, closes database connections, shuts down cleanly
- **PostgreSQL**: Flushes data to disk, closes all connections
- **Nginx/Vite**: Stops serving requests immediately

#### Step 2: **Container Removal**
```
Removing radioawa-frontend-dev
Removing radioawa-backend-dev
Removing radioawa-postgres-dev
```

**What Docker Does:**
1. Deletes container instances (containers are just processes)
2. **Does NOT delete**:
   - Docker images (can be reused)
   - Docker volumes (data persists)
   - Source code (bind mounts are links, not copies)

#### Step 3: **Network Removal**
```
Removing network radioawa_radioawa-network
```

**What Docker Does:**
1. Disconnects all containers from network
2. Deletes the virtual network
3. Frees up the IP address range

#### Step 4: **What Remains**

After `docker compose down`, these **persist**:
- ✅ Docker images (cached for fast restart)
- ✅ Named volumes (`postgres-data`, `maven-cache`)
- ✅ Your source code
- ✅ Environment files (`.env`)

**To restart:**
```bash
docker compose up
# Fast startup using cached images and existing volumes
```

---

### What Happens During `docker compose down -v`

The `-v` flag adds **volume deletion**:

```
Removing radioawa-frontend-dev
Removing radioawa-backend-dev
Removing radioawa-postgres-dev
Removing network radioawa_radioawa-network
Removing volume radioawa_postgres-data  ⚠️ WARNING: Database deleted!
Removing volume radioawa_maven-cache
```

**⚠️ DANGER**: This **permanently deletes** all database data!

**Use cases:**
- Fresh start with clean database
- Testing database migrations
- Clearing corrupted data

**DON'T use in production** unless you have backups!

---

### Development vs Production Builds

#### Development Mode (`docker compose.yml`)

**What Gets Built:**
```
backend:
  - Uses maven:3.9-eclipse-temurin-17 (full JDK + Maven)
  - Source code mounted as volume: ./backend/src → /app/src
  - Runs: mvn spring-boot:run (with DevTools hot reload)
  - Size: ~800MB

frontend:
  - Uses node:20-alpine (full Node.js + npm)
  - Source code mounted as volume: ./frontend/src → /app/src
  - Runs: npm run dev (Vite dev server with HMR)
  - Size: ~400MB
```

**Benefits:**
- **Hot Reload**: Change code, see results instantly (no rebuild)
- **Debugging**: Full toolchain available inside container
- **Fast Iteration**: Edit → Save → See changes in <2 seconds

**Trade-offs:**
- Larger images (includes build tools)
- Higher memory usage

#### Production Mode (`docker compose.prod.yml`)

**What Gets Built:**
```
backend:
  - Stage 1: maven:3.9 (builds JAR, then discarded)
  - Stage 2: eclipse-temurin:17-jre (runtime only)
  - Runs: java -jar app.jar (optimized JVM settings)
  - Size: ~250MB (70% smaller!)

frontend:
  - Stage 1: node:20-alpine (builds static files, then discarded)
  - Stage 2: nginx:alpine (serves pre-built files)
  - Runs: nginx (highly optimized web server)
  - Size: ~40MB (90% smaller!)
```

**Benefits:**
- **Minimal Size**: Faster downloads, less storage
- **Optimized Performance**: Compiled/minified code
- **Security**: No build tools in production (smaller attack surface)
- **Resource Efficient**: Lower memory/CPU usage

**Trade-offs:**
- No hot reload (must rebuild for changes)
- Longer build times

---

### Understanding Container States

Containers can be in different states:

```bash
docker compose ps
```

**Possible States:**

| State | Meaning | What to Do |
|-------|---------|-----------|
| `Up (healthy)` | Running and passing health checks | ✅ All good! |
| `Up` | Running but no health check configured | Check logs if issues |
| `Up (unhealthy)` | Running but failing health checks | Check logs: `docker compose logs service-name` |
| `Restarting` | Continuously crashing and restarting | Fix issue, then `docker compose restart service-name` |
| `Exited (0)` | Stopped successfully | Start with `docker compose up service-name` |
| `Exited (1)` | Crashed/errored | Check logs, fix error, restart |

---

### Beginner Tips: Docker Mental Model

**Think of Docker as:**

1. **Images = Recipes** 📋
   - Instructions to build an application environment
   - Immutable (don't change once built)
   - Can be shared and reused

2. **Containers = Running Instances** 🍳
   - Created from images (like cooking from a recipe)
   - Isolated processes with own filesystem
   - Can be started, stopped, deleted

3. **Volumes = Storage** 💾
   - Persist data beyond container lifecycle
   - Shared between containers
   - Backed up independently

4. **Networks = Communication Channels** 🌐
   - Allow containers to talk to each other
   - Provide DNS resolution by service name
   - Isolated from host network (unless ports mapped)

**Analogy:**
```
Image (Dockerfile)     = Blueprint for a house
Container              = House built from blueprint
Volume                 = Garage (storage persists if house rebuilt)
Network                = Street connecting houses
```

---

## Development vs Production: Standard Practice

### Overview

RadioAwa uses **multi-stage Docker builds** and **separate compose files** to provide optimized configurations for both development and production environments. This is considered **Docker best practice** and ensures:

- **Fast iteration** in development with hot reload
- **Optimized performance** in production with minimal images
- **Same technology stack** across environments (consistency)
- **Different optimizations** for each use case (flexibility)

### Architecture Comparison

```
┌─────────────────────────────────────────────────────────────────┐
│                    DEVELOPMENT MODE                             │
├─────────────────────────────────────────────────────────────────┤
│  Frontend: Vite Dev Server (Port 5171)                         │
│  - Hot Module Replacement (HMR)                                 │
│  - Source maps for debugging                                    │
│  - NOT using nginx.conf                                         │
│  - Image: node:20-alpine (~400MB)                              │
│                                                                  │
│  Backend: Spring Boot DevTools (Port 8081)                     │
│  - Auto-reload on code changes                                  │
│  - Debug logging enabled                                        │
│  - Image: maven:3.9 (~800MB)                                   │
│                                                                  │
│  Database: PostgreSQL (Port 5432 - EXPOSED)                    │
│  - Accessible for direct queries                                │
│  - Sample data for testing                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    PRODUCTION MODE                              │
├─────────────────────────────────────────────────────────────────┤
│  Frontend: Nginx (Port 80)                                      │
│  - Serving pre-built static files                               │
│  - Gzip compression enabled                                     │
│  - USING nginx.conf (security headers, caching, API proxy)     │
│  - Image: nginx:alpine (~40MB - 90% smaller!)                  │
│                                                                  │
│  Backend: Spring Boot JAR (Port 8081 - INTERNAL ONLY)         │
│  - Compiled, optimized JAR                                      │
│  - Production logging (INFO level)                              │
│  - Image: eclipse-temurin:17-jre (~250MB - 70% smaller!)      │
│                                                                  │
│  Database: PostgreSQL (Port 5432 - INTERNAL ONLY)             │
│  - Not exposed externally (security)                            │
│  - Production data with backups                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Key Differences

| Aspect | Development Mode | Production Mode |
|--------|-----------------|-----------------|
| **Frontend Server** | Vite dev server (localhost:5171) | Nginx (localhost:80) |
| **Frontend Config** | `vite.config.js` | `nginx.conf` |
| **Hot Reload** | ✅ Yes - instant code changes | ❌ No - requires rebuild |
| **Source Maps** | ✅ Yes - for debugging | ❌ No - minified code |
| **Image Size (Frontend)** | ~400MB (includes Node.js, npm) | ~40MB (Nginx only) |
| **Image Size (Backend)** | ~800MB (includes Maven, JDK) | ~250MB (JRE only) |
| **Build Time** | Fast (~30 seconds with cache) | Slower (~5 minutes initial) |
| **Startup Time** | ~20-30 seconds | ~10 seconds (pre-compiled) |
| **Logging Level** | DEBUG (verbose) | INFO (production-appropriate) |
| **Database Port** | Exposed (5432) for debugging | Hidden (internal network only) |
| **Backend Port** | Exposed (8081) for API testing | Hidden (proxied through Nginx) |
| **CORS Config** | Permissive (localhost:5171) | Strict (production domain) |
| **Volume Mounts** | Yes (source code mounted) | No (immutable containers) |
| **Resource Usage** | Higher (includes build tools) | Lower (runtime only) |
| **Security** | Relaxed (all ports open) | Hardened (minimal exposure) |

### How Multi-Stage Builds Enable Both Modes

The `frontend/Dockerfile` contains **multiple targets** that are selected based on the docker-compose file used:

```dockerfile
# ============================================
# Stage 1: DEVELOPMENT
# ============================================
FROM node:20-alpine AS development
WORKDIR /app
RUN npm install -g npm@latest
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
EXPOSE 5171
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
# ↑ Used by: docker-compose.yml (target: development)
# ↑ nginx.conf is NOT used in this stage

# ============================================
# Stage 2: BUILD (Production Compilation)
# ============================================
FROM node:20-alpine AS build
WORKDIR /app
RUN npm install -g npm@latest
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build
# ↑ Creates /app/dist with minified, optimized files
# ↑ This stage is discarded after build (not in final image)

# ============================================
# Stage 3: PRODUCTION
# ============================================
FROM nginx:alpine AS production
# Copy built static files from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf
# ↑ nginx.conf is ONLY used here (production stage)

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/health || exit 1
CMD ["nginx", "-g", "daemon off;"]
# ↑ Used by: docker-compose.prod.yml (target: production)
```

### Selecting the Right Mode

**Development mode** is selected by `docker-compose.yml`:
```yaml
services:
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      target: development  # ← Selects development stage
```

**Production mode** is selected by `docker-compose.prod.yml`:
```yaml
services:
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      target: production  # ← Selects production stage
```

### Standard Development Workflow

#### Daily Development (Recommended Approach)

```bash
# 1. Start development stack
docker compose up

# 2. Access application
# - Frontend: http://localhost:5171 (Vite dev server)
# - Backend API: http://localhost:8081
# - Database: localhost:5432

# 3. Edit code in your IDE
# - Frontend changes: Instant hot reload (HMR)
# - Backend changes: Auto-reload (~3-5 seconds)

# 4. View logs
docker compose logs -f

# 5. Stop when done
docker compose down
```

**Why this works:**
- Source code is **mounted as volumes** from host → container
- Vite watches for file changes and applies HMR
- Spring Boot DevTools detects changes and recompiles
- **nginx.conf is NOT loaded** (Vite handles everything)

#### Alternative: Run Without Docker (Local Development)

```bash
# Terminal 1: Frontend
cd frontend
npm install
npm run dev
# Runs on http://localhost:5171

# Terminal 2: Backend
cd backend
mvn spring-boot:run
# Runs on http://localhost:8081

# Terminal 3: Database (still use Docker)
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=radioawa \
  -e POSTGRES_USER=radioawa \
  -e POSTGRES_PASSWORD=radioawa_dev_password \
  --name radioawa-db \
  postgres:16-alpine
```

**When to use this:**
- Faster startup (no container overhead)
- IDE debugging tools work natively
- Easier to attach debuggers

### Standard Production Workflow

#### Pre-Deployment Testing (Test Prod Build Locally)

```bash
# 1. Build production images
docker compose -f docker-compose.prod.yml build

# 2. Test with development credentials (safer)
docker compose -f docker-compose.prod.yml --env-file .env.docker.dev up

# 3. Access production build
open http://localhost

# 4. Verify production features:
# - Check DevTools Network tab (files should be minified: main-abc123.js)
# - Verify health check: curl http://localhost/health
# - Test API proxy: curl http://localhost/api/stations
# - Confirm no CORS errors (same origin via Nginx)
# - Check response headers for security headers

# 5. Stop test deployment
docker compose -f docker-compose.prod.yml down
```

#### Production Deployment

```bash
# On production server:

# 1. Configure production environment
cp .env.docker.prod .env.prod
nano .env.prod
# CHANGE:
# - DB_PASSWORD (strong password!)
# - CORS_ALLOWED_ORIGINS (https://yourdomain.com)

# 2. Build optimized images
docker compose -f docker-compose.prod.yml build --no-cache

# 3. Start production stack
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d

# 4. Verify deployment
docker compose -f docker-compose.prod.yml ps
# All services should show "Up (healthy)"

# 5. Monitor logs
docker compose -f docker-compose.prod.yml logs -f

# 6. Set up automated backups (see section below)
```

### Best Practices Summary

#### ✅ DO

1. **Use development mode for daily work**
   - Fast iteration with hot reload
   - Full debugging capabilities
   - All ports accessible for testing

2. **Test production builds locally before deploying**
   - Catches build issues early
   - Verifies Nginx configuration
   - Tests minified/optimized code

3. **Keep separate environment files**
   - `.env.docker.dev` for development
   - `.env.docker.prod` for production
   - Never commit `.env.prod` to git

4. **Use docker-compose.yml for dev, docker-compose.prod.yml for production**
   - Clear separation of concerns
   - Prevents accidental production deployment with dev config

5. **Mount source code only in development**
   - Enables hot reload
   - Keep production containers immutable

#### ❌ DON'T

1. **Don't use production mode for development**
   - No hot reload (slow iteration)
   - Requires full rebuild for every change
   - Harder to debug (no source maps)

2. **Don't use development mode in production**
   - Larger images (wasted resources)
   - Security risk (source code exposed)
   - Slower performance (dev server overhead)

3. **Don't mix configurations**
   - Don't use Nginx in development
   - Don't use Vite dev server in production
   - Don't expose database port in production

4. **Don't skip testing production builds**
   - Always test prod build locally first
   - Catches configuration issues early
   - Verifies nginx.conf is working

### Verifying Your Current Mode

```bash
# Check which mode is running
docker compose ps

# Development mode shows:
# - radioawa-frontend-dev (port 5171)
# - Command: npm run dev

# Production mode shows:
# - radioawa-frontend-prod (port 80)
# - Command: nginx -g daemon off;
```

### Nginx Configuration in Production Only

The `nginx.conf` file provides production-specific features:

**Features ONLY in Production:**
- ✅ **API Reverse Proxy**: `/api/*` → `http://backend:8081`
- ✅ **Security Headers**: X-Frame-Options, X-Content-Type-Options, etc.
- ✅ **Gzip Compression**: Optimizes response sizes
- ✅ **Static Asset Caching**: 1-year cache for JS/CSS/images
- ✅ **SPA Routing**: All routes serve `index.html`
- ✅ **Health Check Endpoint**: `/health` for monitoring

**NOT Used in Development:**
- ❌ Vite dev server handles all routing
- ❌ Vite proxy handles API requests
- ❌ No caching needed (development)
- ❌ No compression needed (local)

### Quick Reference Commands

| Task | Command |
|------|---------|
| **Start development** | `docker compose up` |
| **Start development (background)** | `docker compose up -d` |
| **Stop development** | `docker compose down` |
| **View dev logs** | `docker compose logs -f` |
| **Restart dev service** | `docker compose restart frontend` |
| **Build production images** | `docker compose -f docker-compose.prod.yml build` |
| **Test production locally** | `docker compose -f docker-compose.prod.yml up` |
| **Deploy to production** | `docker compose -f docker-compose.prod.yml --env-file .env.prod up -d` |
| **View production logs** | `docker compose -f docker-compose.prod.yml logs -f` |
| **Stop production** | `docker compose -f docker-compose.prod.yml down` |
| **Check which mode running** | `docker compose ps` |

### Troubleshooting Mode Confusion

**Problem:** "I changed code but don't see changes"

**Solution:**
```bash
# Check if you're in production mode by mistake
docker compose ps

# If it shows nginx on port 80, you're in production mode
# Stop production, start development:
docker compose -f docker-compose.prod.yml down
docker compose up
```

**Problem:** "nginx.conf changes not working in development"

**Solution:**
- nginx.conf is **only used in production mode**
- Development uses Vite dev server (configured in `vite.config.js`)
- To test nginx.conf, use: `docker compose -f docker-compose.prod.yml up`

**Problem:** "Production build is slow"

**Solution:**
- This is expected! Production builds are optimized for runtime, not build time
- Use development mode for daily work
- Only build production when deploying or testing prod features

---

## Development Deployment

### Quick Start (Fastest Method)

#### Option 1: Interactive Setup Script
```bash
./docker-setup.sh
# Select: 1) Development
# Script handles everything automatically
```

#### Option 2: Manual Setup
```bash
# Step 1: Copy environment file
cp .env.docker.dev .env

# Step 2: Start all services
docker compose up

# Step 3: Access the application
# Frontend: http://localhost:5171
# Backend API: http://localhost:8081
```

#### Option 3: Background Mode (Detached)
```bash
docker compose up -d
# Services run in background

# View logs
docker compose logs -f
```

### What You'll See

**Console Output:**
```
[+] Running 4/4
 ✔ Network radioawa_radioawa-network  Created
 ✔ Volume radioawa_postgres-data      Created
 ✔ Container radioawa-postgres-dev    Started (healthy)
 ✔ Container radioawa-backend-dev     Started
 ✔ Container radioawa-frontend-dev    Started
```

**Access Points:**
- **Frontend**: http://localhost:5171
  - Radio player with station selector
  - Switch between English and Hindi stations
  - Song ratings and now playing display

- **Backend API**: http://localhost:8081
  - `GET /api/stations` - List stations
  - `GET /api/health` - Health check

- **Database**: localhost:5432
  - User: `radioawa`
  - Password: `radioawa_dev_password`
  - Database: `radioawa`

### Development Features

#### 🔥 Hot Reload Enabled

**Backend (Spring Boot DevTools):**
```bash
# Edit any Java file in backend/src/
# Spring Boot automatically recompiles and reloads
# See changes in ~3-5 seconds (no restart needed!)
```

**Frontend (Vite HMR):**
```bash
# Edit any React component in frontend/src/
# Vite injects changes instantly with Hot Module Replacement
# See changes in <1 second!
```

#### 📁 Volume Mounts

Development mode mounts your source code:
```yaml
volumes:
  - ./backend/src:/app/src      # Live backend code
  - ./frontend/src:/app/src      # Live frontend code
```

**This means:**
- Edit files in your IDE (VSCode, IntelliJ, etc.)
- Changes immediately visible in container
- No need to rebuild images for code changes

#### 📊 Debug Logging

Verbose logging enabled for troubleshooting:
- Spring Boot: `DEBUG` level logging
- PostgreSQL: SQL query logging
- Vite: Request logging and HMR status

### Development Commands

#### Starting Services
```bash
# Start and show logs
docker compose up

# Start in background (detached)
docker compose up -d

# Rebuild images before starting
docker compose up --build

# Start specific service only
docker compose up postgres backend
```

#### Viewing Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres

# Last 100 lines
docker compose logs --tail=100 backend

# Since specific time
docker compose logs --since="2024-12-19T10:00:00"
```

#### Stopping Services
```bash
# Stop all services (containers remain)
docker compose stop

# Stop and remove containers (volumes persist)
docker compose down

# Stop, remove containers AND volumes (⚠️ deletes database!)
docker compose down -v
```

#### Restarting Services
```bash
# Restart all services
docker compose restart

# Restart specific service
docker compose restart backend
docker compose restart frontend
```

#### Executing Commands Inside Containers
```bash
# Backend: Open bash shell
docker compose exec backend bash

# Frontend: Open shell
docker compose exec frontend sh

# PostgreSQL: Connect to database
docker compose exec postgres psql -U radioawa -d radioawa

# Run Maven tests
docker compose exec backend mvn test

# Check Node version
docker compose exec frontend node --version
```

#### Database Operations
```bash
# Connect to PostgreSQL
docker compose exec postgres psql -U radioawa -d radioawa

# List all stations
docker compose exec postgres psql -U radioawa -d radioawa \
  -c "SELECT code, name, is_active FROM stations;"

# View songs
docker compose exec postgres psql -U radioawa -d radioawa \
  -c "SELECT * FROM songs LIMIT 10;"

# Check database size
docker compose exec postgres psql -U radioawa -d radioawa \
  -c "SELECT pg_size_pretty(pg_database_size('radioawa'));"
```

### Testing Your Setup

#### 1. Test Database
```bash
curl -s http://localhost:8081/api/stations | python3 -m json.tool
```

**Expected Output:**
```json
[
  {
    "id": 1,
    "code": "ENGLISH",
    "name": "RadioAwa English",
    "isActive": true
  },
  {
    "id": 2,
    "code": "HINDI",
    "name": "RadioAwa Hindi",
    "isActive": true
  }
]
```

#### 2. Test Frontend
```bash
curl -I http://localhost:5171
```

**Expected Output:**
```
HTTP/1.1 200 OK
Content-Type: text/html
```

#### 3. Test Hot Reload (Backend)
```bash
# 1. Edit backend/src/main/java/com/radioawa/controller/HealthController.java
# 2. Add a log statement or change return value
# 3. Watch logs:
docker compose logs -f backend
# You'll see: "Restarting due to 1 class changes"
```

#### 4. Test Hot Reload (Frontend)
```bash
# 1. Edit frontend/src/App.jsx
# 2. Change some text
# 3. Browser automatically updates (check console for HMR message)
```

---

## Production Deployment

### Security Checklist (CRITICAL!)

Before deploying to production, complete this checklist:

- [ ] **Changed default passwords** (especially `DB_PASSWORD`)
- [ ] **Updated CORS origins** to your actual domain
- [ ] **Configured SSL/TLS** certificate (use Caddy, Traefik, or Nginx reverse proxy)
- [ ] **Reviewed exposed ports** (only port 80/443 should be public)
- [ ] **Set up database backups** (automated daily backups)
- [ ] **Configured monitoring** (health checks, alerts)
- [ ] **Tested disaster recovery** (can you restore from backup?)
- [ ] **Limited container resources** (CPU/memory limits set)
- [ ] **Enabled logging to external service** (CloudWatch, Datadog, etc.)

### Production Setup Steps

#### 1. Copy and Configure Environment
```bash
# Copy template
cp .env.docker.prod .env.prod

# ⚠️ IMPORTANT: Edit with production values
nano .env.prod
```

**Edit `.env.prod`:**
```bash
# CHANGE THESE VALUES!
DB_NAME=radioawa_production
DB_USER=radioawa_prod
DB_PASSWORD=YOUR_VERY_STRONG_PASSWORD_HERE_MIN_20_CHARS

# Your actual domain
CORS_ALLOWED_ORIGINS=https://radioawa.com,https://www.radioawa.com

# Optional: Custom ports
FRONTEND_PORT=80
BACKEND_PORT=8081
```

#### 2. Build Production Images
```bash
docker compose -f docker-compose.prod.yml build --no-cache

# This will:
# - Build optimized backend JAR
# - Build minified frontend bundle
# - Create production Nginx image
# Time: ~5-10 minutes
```

#### 3. Start Production Services
```bash
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Services start in this order:
# 1. PostgreSQL (waits for healthy)
# 2. Backend (waits for PostgreSQL)
# 3. Frontend (starts independently)
```

#### 4. Verify Deployment
```bash
# Check all services running
docker compose -f docker-compose.prod.yml ps

# Expected output:
# NAME                       STATUS
# radioawa-postgres-prod     Up (healthy)
# radioawa-backend-prod      Up
# radioawa-frontend-prod     Up (healthy)
```

#### 5. Test Health Endpoints
```bash
# Backend health check
curl http://localhost:8081/actuator/health

# Expected: {"status":"UP"}

# Frontend serving
curl -I http://localhost:80

# Expected: HTTP/1.1 200 OK
```

### Production Features

#### Optimized Builds
- **Backend**: Compiled JAR with minimal JRE runtime
- **Frontend**: Minified, tree-shaken JavaScript and CSS
- **Total size reduction**: 70-90% smaller than development images

#### Security Hardening
- **Non-root user**: Backend runs as `radioawa` user (not root)
- **No exposed ports**: Database and backend not accessible from internet
- **Minimal images**: Alpine Linux base reduces attack surface
- **Health monitoring**: Automatic restart if services become unhealthy

#### Performance Optimization
- **JVM tuning**: Optimized garbage collection and memory settings
- **Connection pooling**: HikariCP configured for production load
- **Nginx caching**: Static assets cached with proper headers
- **Compression**: Gzip enabled for all text responses

### Production Commands

#### Starting & Stopping
```bash
# Start (background mode)
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d

# View logs
docker compose -f docker-compose.prod.yml logs -f

# Stop services
docker compose -f docker-compose.prod.yml down
```

#### Health Monitoring
```bash
# Check service status
docker compose -f docker-compose.prod.yml ps

# View resource usage
docker stats

# Test endpoints
curl http://localhost:8081/actuator/health
curl -I http://localhost:80
```

#### Database Backups
```bash
# Create backup
docker compose -f docker-compose.prod.yml exec postgres \
  pg_dump -U radioawa radioawa > backup-$(date +%Y%m%d-%H%M%S).sql

# Restore from backup
docker compose -f docker-compose.prod.yml exec -T postgres \
  psql -U radioawa -d radioawa < backup-20241219-120000.sql

# Automated backup script (add to cron)
#!/bin/bash
BACKUP_DIR=/backups
DATE=$(date +%Y%m%d-%H%M%S)
docker compose -f docker-compose.prod.yml exec postgres \
  pg_dump -U radioawa radioawa > $BACKUP_DIR/backup-$DATE.sql
# Keep only last 7 days
find $BACKUP_DIR -name "backup-*.sql" -mtime +7 -delete
```

#### Zero-Downtime Updates
```bash
# Pull latest images
docker compose -f docker-compose.prod.yml pull

# Rebuild and restart (with minimal downtime)
docker compose -f docker-compose.prod.yml up -d --build --force-recreate

# Or use rolling update (if multiple instances):
docker compose -f docker-compose.prod.yml up -d --no-deps backend
docker compose -f docker-compose.prod.yml up -d --no-deps frontend
```

#### Scaling Services
```bash
# Run multiple backend instances (if using load balancer)
docker compose -f docker-compose.prod.yml up -d --scale backend=3

# Note: Database should NOT be scaled (use replication instead)
```

---

## Docker Commands Reference

### Building Images

```bash
# Build all services
docker compose build

# Build specific service
docker compose build backend

# Build without using cache (fresh build)
docker compose build --no-cache

# Build and pull base images
docker compose build --pull

# Build production images
docker compose -f docker-compose.prod.yml build
```

### Managing Services

```bash
# Start all services (foreground)
docker compose up

# Start in background (detached)
docker compose up -d

# Start and rebuild changed services
docker compose up -d --build

# Stop all services (keeps containers)
docker compose stop

# Stop specific service
docker compose stop backend

# Remove stopped containers
docker compose down

# Remove containers AND volumes (⚠️ deletes data)
docker compose down -v

# Remove containers, volumes, AND images
docker compose down -v --rmi all
```

### Viewing Information

```bash
# List running containers
docker compose ps

# List all containers (including stopped)
docker compose ps -a

# View logs (all services)
docker compose logs

# Follow logs (live tail)
docker compose logs -f

# Logs for specific service
docker compose logs -f backend

# Last N lines
docker compose logs --tail=50 backend

# View service configuration
docker compose config

# Show images used
docker compose images
```

### Monitoring & Debugging

```bash
# View resource usage (CPU, memory, network)
docker stats

# View top processes in containers
docker compose top

# Inspect container details
docker inspect radioawa-backend-dev

# View container events
docker compose events

# View networks
docker network ls
docker network inspect radioawa_radioawa-network

# View volumes
docker volume ls
docker volume inspect radioawa_postgres-data
```

### Executing Commands

```bash
# Run command in service
docker compose exec backend bash
docker compose exec postgres psql -U radioawa

# Run one-off command (creates new container)
docker compose run --rm backend mvn test

# Run as different user
docker compose exec -u root backend bash

# Copy files to/from container
docker cp local-file.txt radioawa-backend-dev:/tmp/
docker cp radioawa-backend-dev:/tmp/file.txt ./
```

### Cleaning Up

```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune

# Remove unused networks
docker network prune

# Remove everything unused (⚠️ nuclear option)
docker system prune -a --volumes

# Check disk usage
docker system df
```

---

## Container Images Explained

### Multi-Stage Builds: Why and How

**Problem**: Build tools (Maven, npm) are large but only needed during compilation, not at runtime.

**Solution**: Multi-stage Dockerfiles build in one image, copy artifacts to a minimal runtime image.

**Benefits**:
- Smaller images (faster downloads, less storage)
- More secure (fewer attack vectors)
- Cleaner separation of build vs. runtime

### Backend Image Architecture

```dockerfile
# Stage 1: BUILD (maven:3.9-eclipse-temurin-17-alpine)
# Purpose: Compile Java code to JAR
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline          # Download dependencies
COPY src ./src
RUN mvn clean package -DskipTests      # Build JAR
# Result: /app/target/radioawa-backend-0.0.1-SNAPSHOT.jar
# Image size: ~500MB (discarded after build)

# Stage 2: DEVELOPMENT (maven:3.9-eclipse-temurin-17)
# Purpose: Development environment with hot reload
FROM maven:3.9-eclipse-temurin-17 AS development
WORKDIR /app
COPY pom.xml .
COPY src ./src
CMD ["mvn", "spring-boot:run"]
# Image size: ~800MB
# Features: Full Maven, JDK, DevTools hot reload

# Stage 3: PRODUCTION (eclipse-temurin:17-jre)
# Purpose: Minimal runtime for JAR execution
FROM eclipse-temurin:17-jre AS production
WORKDIR /app
RUN addgroup -S radioawa && adduser -S radioawa -G radioawa
COPY --from=build /app/target/*.jar app.jar
USER radioawa
ENTRYPOINT ["java", "-jar", "app.jar"]
# Image size: ~250MB (70% reduction!)
# Features: JRE only, non-root user, optimized startup
```

**Key Insights:**
- Stage 1 builds JAR, then is **thrown away**
- Stage 2 used only in development mode
- Stage 3 copies JAR from Stage 1 (doesn't rebuild)
- Production image has **no source code or build tools**

### Frontend Image Architecture

```dockerfile
# Stage 1: DEVELOPMENT (node:20-alpine)
# Purpose: Vite dev server with HMR
FROM node:20-alpine AS development
WORKDIR /app
RUN npm install -g npm@latest
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
# Image size: ~400MB
# Features: Node.js, npm, Vite, HMR

# Stage 2: BUILD (node:20-alpine)
# Purpose: Compile and bundle React code
FROM node:20-alpine AS build
WORKDIR /app
RUN npm install -g npm@latest
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build
# Result: /app/dist/ (minified HTML, CSS, JS)
# Image size: ~400MB (discarded after build)

# Stage 3: PRODUCTION (nginx:alpine)
# Purpose: Serve static files with Nginx
FROM nginx:alpine AS production
COPY --from=build /app/dist /usr/share/nginx/html
# Copy custom nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
# Image size: ~40MB (90% reduction!)
# Features: Highly optimized Nginx, gzip, caching, API proxy, security headers
```

**Key Insights:**
- Stage 1 runs Vite dev server (development only)
- Stage 2 builds production bundle, then is **thrown away**
- Stage 3 copies built files from Stage 2 (no Node.js!)
- Production image is just Nginx + static files

### Database Image

```dockerfile
# Official PostgreSQL image
FROM postgres:16-alpine

# Features:
# - PostgreSQL 16 on Alpine Linux
# - Automatic initialization scripts
# - Environment variable configuration
# - Built-in health checks

# Image size: ~230MB
```

**Initialization Process:**
1. On first start, PostgreSQL creates database cluster
2. Runs all `.sql` files in `/docker-entrypoint-initdb.d/`
3. Our `multi-station-migration.sql` runs, creating:
   - Stations table (English & Hindi)
   - Songs table
   - Ratings table
4. Subsequent starts skip initialization (data already exists)

---

## Networking & Communication

### Docker Network Architecture

RadioAwa uses a custom **bridge network** for inter-service communication:

```
┌──────────────────────────────────────────────────────────┐
│                  radioawa-network                        │
│                  (Bridge: 172.18.0.0/16)                 │
│                                                          │
│  ┌─────────────┐   ┌─────────────┐   ┌──────────────┐  │
│  │  Frontend   │──▶│  Backend    │──▶│  PostgreSQL  │  │
│  │  172.18.0.4 │   │  172.18.0.3 │   │  172.18.0.2  │  │
│  │  :5171      │   │  :8081      │   │  :5432       │  │
│  └──────┬──────┘   └─────────────┘   └──────────────┘  │
│         │                                                │
└─────────┼────────────────────────────────────────────────┘
          │ (Port Mapping)
          ▼
    Host Machine
    localhost:5171
```

### Service Discovery (DNS)

Docker provides **automatic DNS resolution** within the network:

```bash
# Inside frontend container:
curl http://backend:8081/api/stations
# Docker resolves "backend" to 172.18.0.3

# Inside backend container:
psql -h postgres -p 5432 -U radioawa
# Docker resolves "postgres" to 172.18.0.2
```

**How It Works:**
1. Docker runs an internal DNS server
2. Service names (from `docker-compose.yml`) become DNS entries
3. Containers query Docker's DNS (127.0.0.11) for name resolution
4. No need to hardcode IP addresses!

### Port Mapping Explained

**Development Mode:**
```yaml
services:
  frontend:
    ports:
      - "5171:5171"  # Host:Container
  backend:
    ports:
      - "8081:8081"
  postgres:
    ports:
      - "5432:5432"
```

**What This Means:**
- `localhost:5171` on your machine → port 5171 in frontend container
- Allows you to access services from your browser/tools
- All ports exposed for easy debugging

**Production Mode:**
```yaml
services:
  frontend:
    ports:
      - "80:80"      # Only frontend exposed
  backend:
    expose:
      - "8081"       # Internal only (not mapped to host)
  postgres:
    expose:
      - "5432"       # Internal only
```

**Security Benefit:**
- Only frontend accessible from internet
- Backend and database hidden from external access
- Reduces attack surface

### Communication Flow

**User Request Flow:**
```
1. User Browser
   │
   ▼
2. localhost:5171 (host)
   │ (Port mapping)
   ▼
3. Frontend Container :5171
   │ (API call to /api/stations)
   ▼
4. Backend Container :8081
   │ (Database query)
   ▼
5. PostgreSQL Container :5432
   │ (Result)
   ▼
6. Back to user (JSON response)
```

**Internal Communication:**
```bash
# Frontend → Backend (inside Docker network)
http://backend:8081/api/stations

# Backend → Database (inside Docker network)
jdbc:postgresql://postgres:5432/radioawa
```

**Key Point**: Services use **service names** (not `localhost`) to communicate internally!

---

## Data Persistence

### Understanding Docker Volumes

**Problem**: Container filesystems are **ephemeral** (deleted when container is removed).

**Solution**: **Volumes** persist data outside the container lifecycle.

### Volume Types in RadioAwa

#### 1. Named Volumes (Production Data)

```yaml
volumes:
  postgres-data:
    driver: local  # Stored on Docker host

services:
  postgres:
    volumes:
      - postgres-data:/var/lib/postgresql/data
```

**Characteristics:**
- Managed by Docker
- Persist even if containers are deleted
- Shared across container restarts
- Backed up independently

**Location:**
- **Mac**: `~/Library/Containers/com.docker.docker/Data/vms/0/`
- **Linux**: `/var/lib/docker/volumes/`
- **Windows**: `C:\ProgramData\Docker\volumes\`

**View Volume:**
```bash
docker volume inspect radioawa_postgres-data
```

#### 2. Bind Mounts (Development Code)

```yaml
services:
  backend:
    volumes:
      - ./backend/src:/app/src
```

**Characteristics:**
- Maps host directory directly into container
- Changes on host instantly visible in container
- Used for hot reload in development
- **Not used in production** (security risk)

**What Gets Mounted:**
```
Host                Container
./backend/src    →  /app/src
./frontend/src   →  /app/src
```

**Benefit**: Edit in IDE, see changes in running container immediately!

### What Persists vs. What Doesn't

**✅ Persists (Survives `docker compose down`):**
- PostgreSQL database (in `postgres-data` volume)
- Maven dependencies cache (in `maven-cache` volume)
- Your source code (on host machine, not in container)
- Docker images (cached for reuse)

**❌ Doesn't Persist (Lost on `docker compose down`):**
- Running processes (containers are stopped)
- Temporary files in `/tmp` inside containers
- Logs (unless mounted to volume)
- Network state

**⚠️ Deleted with `docker compose down -v`:**
- **All volumes** including database!
- Use `-v` only for testing fresh setups

### Backup Strategies

#### Database Backup (Recommended)

**1. SQL Dump (Portable):**
```bash
# Create backup
docker compose exec postgres pg_dump -U radioawa radioawa > backup.sql

# Restore
docker compose exec -T postgres psql -U radioawa -d radioawa < backup.sql
```

**2. Volume Backup (Fast):**
```bash
# Stop database first (ensures consistency)
docker compose stop postgres

# Backup volume to tar
docker run --rm \
  -v radioawa_postgres-data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup-$(date +%Y%m%d).tar.gz /data

# Restart database
docker compose start postgres
```

**3. Automated Backup (Cron Job):**
```bash
# Add to crontab (daily at 2 AM)
0 2 * * * cd /path/to/radioawa && docker compose exec postgres pg_dump -U radioawa radioawa > /backups/db-$(date +\%Y\%m\%d).sql
```

### Volume Inspection & Management

```bash
# List all volumes
docker volume ls

# Inspect volume details
docker volume inspect radioawa_postgres-data

# Check volume disk usage
docker system df -v

# Remove unused volumes (⚠️ careful!)
docker volume prune

# Remove specific volume (⚠️ deletes data!)
docker volume rm radioawa_postgres-data
```

---

## Docker Best Practices for Beginners

This section provides practical Docker best practices explained in beginner-friendly language, using examples from the RadioAwa project.

### 1. Understanding .dockerignore

**What**: `.dockerignore` is like `.gitignore` but for Docker builds.

**Why**: Prevents unnecessary files from being sent to Docker during builds (faster builds, smaller images).

**Example from RadioAwa:**
```
# backend/.dockerignore
target/              # Maven build output (rebuilt inside container)
*.log               # Log files (not needed in image)
.git/               # Git history (huge, not needed)
node_modules/       # Dependencies (reinstalled inside container)
```

**Impact**: Without `.dockerignore`, builds would be **3-5x slower** because Docker copies everything!

**Best Practice:**
- Always create `.dockerignore` before `Dockerfile`
- Exclude: build outputs, dependencies, logs, IDE files, git history

### 2. Layer Caching (Why Order Matters)

**Concept**: Docker caches each line in a Dockerfile. If a line doesn't change, Docker reuses the cached result.

**Example:**
```dockerfile
# ❌ BAD: Source code changes break cache
FROM maven:3.9
WORKDIR /app
COPY . .                    # Everything copied
RUN mvn dependency:resolve  # Dependencies downloaded EVERY TIME

# ✅ GOOD: Dependencies cached separately
FROM maven:3.9
WORKDIR /app
COPY pom.xml .             # Only dependency manifest
RUN mvn dependency:resolve # Cached if pom.xml unchanged
COPY src ./src             # Source code copied last
RUN mvn package            # Only recompiles if src changed
```

**Rule**: Order Dockerfile from **least frequently changing** to **most frequently changing**.

**Typical Order:**
1. Base image (rarely changes)
2. System packages (rarely changes)
3. Dependency manifests (changes occasionally)
4. Dependencies installation (cached if manifest unchanged)
5. Source code (changes frequently)
6. Build command (only runs if source changed)

### 3. Multi-Stage Builds (Keep Images Small)

**Problem**: Build tools are large but not needed at runtime.

**Example from RadioAwa Frontend:**
```dockerfile
# Stage 1: Build (node:20-alpine) - ~400MB
FROM node:20-alpine AS build
RUN npm install --legacy-peer-deps
RUN npm run build
# Result: /app/dist/ folder

# Stage 2: Production (nginx:alpine) - ~40MB
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Node.js and npm NOT included in final image!
```

**Benefit**: Final image is **90% smaller** because it doesn't include Node.js!

**Best Practice:**
- Use multi-stage for compiled languages (Java, Go, TypeScript)
- Keep only runtime in final stage
- Use Alpine Linux when possible (smallest base images)

### 4. Don't Run as Root (Security)

**Problem**: Running as root inside containers is a security risk.

**Example from RadioAwa Backend:**
```dockerfile
# ❌ BAD: Runs as root (default)
FROM eclipse-temurin:17-jre
COPY app.jar /app.jar
CMD ["java", "-jar", "/app.jar"]

# ✅ GOOD: Creates non-root user
FROM eclipse-temurin:17-jre
RUN addgroup -S radioawa && adduser -S radioawa -G radioawa
COPY --chown=radioawa:radioawa app.jar /app.jar
USER radioawa  # All commands run as radioawa user
CMD ["java", "-jar", "/app.jar"]
```

**Why It Matters**: If attacker exploits your app, they only have limited user permissions (not root!).

### 5. Health Checks (Know When Services Are Ready)

**Problem**: A running container doesn't mean a working application.

**Example:**
```yaml
# ❌ WITHOUT health check
services:
  postgres:
    image: postgres:16
  backend:
    depends_on:
      - postgres  # Starts after postgres container starts (but DB might not be ready!)

# ✅ WITH health check
services:
  postgres:
    image: postgres:16
    healthcheck:
      test: ["CMD", "pg_isready", "-U", "radioawa"]
      interval: 10s
      timeout: 5s
      retries: 5
  backend:
    depends_on:
      postgres:
        condition: service_healthy  # Waits for PostgreSQL to be ready!
```

**Benefit**: Backend doesn't crash trying to connect to a database that's still initializing.

**Best Practice**: Add health checks to all services that other services depend on.

### 6. Environment Variables (Configuration Without Rebuilds)

**Problem**: Hardcoding configuration in Dockerfiles requires rebuilds for changes.

**Example:**
```dockerfile
# ❌ BAD: Hardcoded in Dockerfile
FROM postgres:16
ENV POSTGRES_DB=radioawa
ENV POSTGRES_PASSWORD=password123  # Can't change without rebuild!

# ✅ GOOD: Use docker-compose.yml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}  # Set in .env file
```

**Benefit**: Change `.env` file to change configuration (no rebuild needed!).

**Best Practice:**
- Never hardcode secrets in Dockerfiles
- Use `.env` files for configuration
- Different `.env` files for dev/prod (`.env.docker.dev`, `.env.docker.prod`)

### 7. Minimize Layers (Combine RUN Commands)

**Concept**: Each `RUN`, `COPY`, `ADD` creates a layer. Fewer layers = smaller images.

**Example:**
```dockerfile
# ❌ BAD: 3 layers
RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y git

# ✅ GOOD: 1 layer
RUN apt-get update && \
    apt-get install -y curl git && \
    rm -rf /var/lib/apt/lists/*  # Clean up in same layer
```

**Best Practice:** Combine related `RUN` commands with `&&`, clean up in the same layer.

### 8. Use Specific Image Tags (Reproducibility)

**Problem**: `latest` tag can change, breaking builds unexpectedly.

**Example:**
```dockerfile
# ❌ BAD: Unpredictable
FROM node:latest  # Could be v18, v20, v22 depending on when you build

# ✅ GOOD: Specific version
FROM node:20-alpine  # Always Node.js 20 on Alpine Linux
```

**Best Practice:**
- Use specific versions: `postgres:16-alpine`, `maven:3.9-eclipse-temurin-17`
- Avoids surprise breakages when `latest` changes

### 9. Logging to STDOUT/STDERR (Not Files)

**Concept**: Docker captures logs from `STDOUT` and `STDERR`.

**Example from Spring Boot:**
```properties
# ❌ BAD: Log to file
logging.file=/var/log/app.log

# ✅ GOOD: Log to console
# (default in Spring Boot - no config needed!)
```

**Why**: `docker compose logs` shows container output; file logs aren't captured.

**Best Practice:** Always log to console in containers (not files).

### 10. Development vs Production Configs

**Concept**: Different optimization for different environments.

**RadioAwa Example:**
```yaml
# Development (docker-compose.yml)
services:
  backend:
    build:
      target: development  # Large image with tools
    volumes:
      - ./backend/src:/app/src  # Hot reload
    environment:
      SPRING_PROFILES_ACTIVE: dev  # Debug logging

# Production (docker-compose.prod.yml)
services:
  backend:
    build:
      target: production  # Minimal image
    # No volume mounts (immutable container)
    environment:
      SPRING_PROFILES_ACTIVE: prod  # Minimal logging
      JAVA_OPTS: "-Xmx512m"  # Memory limits
```

**Best Practice:** Separate compose files for dev and prod, never use dev config in production!

### 11. Resource Limits (Prevent Resource Hogging)

**Problem**: One container can consume all system resources.

**Example:**
```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1.0'      # Max 1 CPU core
          memory: 512M     # Max 512MB RAM
        reservations:
          cpus: '0.5'      # Guaranteed 0.5 CPU
          memory: 256M     # Guaranteed 256MB RAM
```

**Best Practice:** Set limits in production to prevent cascading failures.

### 12. Restart Policies (Auto-Recovery)

**Concept**: Automatically restart crashed containers.

**Example:**
```yaml
services:
  backend:
    restart: unless-stopped  # Restart unless manually stopped

  postgres:
    restart: always          # Always restart (even on reboot)
```

**Options:**
- `no`: Never restart (default)
- `always`: Always restart
- `unless-stopped`: Restart unless manually stopped
- `on-failure`: Only restart on error

**Best Practice:** Use `always` or `unless-stopped` in production.

### 13. Secrets Management (Keep Passwords Safe)

**Problem**: `.env` files with passwords might be committed to git.

**Best Practices:**
```bash
# 1. Add to .gitignore
echo ".env" >> .gitignore
echo ".env.prod" >> .gitignore

# 2. Use Docker secrets (production)
docker secret create db_password password.txt

# docker-compose.yml
secrets:
  db_password:
    external: true
services:
  postgres:
    secrets:
      - db_password
```

**Alternative**: Use external secret managers (AWS Secrets Manager, HashiCorp Vault).

### 14. Regular Image Updates (Security Patches)

**Problem**: Old base images have security vulnerabilities.

**Best Practice:**
```bash
# Monthly: Pull latest base images
docker compose pull

# Rebuild with updated bases
docker compose build --pull

# Check for vulnerabilities (if using Docker Scout)
docker scout cves radioawa-backend
```

### 15. Clean Up Regularly (Save Disk Space)

**Docker accumulates unused data over time:**

```bash
# Weekly cleanup routine:

# Remove stopped containers
docker container prune -f

# Remove unused images
docker image prune -a -f

# Remove unused volumes (⚠️ careful with this!)
docker volume prune -f

# Remove everything unused
docker system prune -a --volumes -f

# Check disk usage
docker system df
```

**Best Practice**: Schedule regular cleanup (cron job or manual weekly).

---

## Known Issues

### Frontend Docker Build - npm Registry Access

**Issue**: In some environments, the frontend Docker build may fail with npm package resolution errors:
```
npm error 404 Not Found - GET https://registry.npmjs.org/@vitest%2futils
```

**Root Cause**: Network/DNS issue between Docker and npm registry, often related to:
- Docker DNS configuration
- Corporate proxy/firewall restrictions
- npm version compatibility

**Workarounds** (try in order):

**1. Configure Docker DNS** (Recommended):
```bash
# Edit /etc/docker/daemon.json (create if doesn't exist)
{
  "dns": ["8.8.8.8", "8.8.4.4"]
}

# Restart Docker
sudo systemctl restart docker  # Linux
# Or restart Docker Desktop on Mac/Windows
```

**2. Use npm mirror:**
```dockerfile
# Add to frontend/Dockerfile before npm install
RUN npm config set registry https://registry.npmmirror.com/
```

**3. Pre-build approach:**
```bash
# Build frontend locally
cd frontend && npm run build

# Simplified Dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
```

**4. Skip frontend container temporarily:**
```bash
# Run only backend and database
docker compose up postgres backend

# Run frontend locally
cd frontend && npm run dev
```

**5. Try different network:**
- Build on personal WiFi instead of corporate network
- Use mobile hotspot
- Try VPN/no VPN

**Note**: This is environment-specific. Backend builds successfully in all tested environments.

---

## Troubleshooting

### Services Won't Start

**Symptom:** `docker compose up` fails

**Diagnosis:**
```bash
# Check Docker is running
docker --version

# Check for port conflicts
lsof -i :5171  # Frontend
lsof -i :8081  # Backend
lsof -i :5432  # Database

# View detailed logs
docker compose logs
```

**Solutions:**
```bash
# Kill conflicting processes
kill -9 $(lsof -t -i:8081)

# Remove old containers and volumes
docker compose down -v

# Rebuild images
docker compose build --no-cache

# Start with verbose output
docker compose up --verbose
```

### Database Connection Errors

**Symptom:** Backend logs show `Connection refused` or `database "radioawa" does not exist`

**Diagnosis:**
```bash
# Check PostgreSQL health
docker compose exec postgres pg_isready -U radioawa

# Check environment variables
docker compose exec backend env | grep DB_

# View PostgreSQL logs
docker compose logs postgres | grep ERROR
```

**Solutions:**
```bash
# Wait for database initialization (first start takes ~15 seconds)
sleep 15

# Restart database
docker compose restart postgres

# Check database exists
docker compose exec postgres psql -U radioawa -l

# Recreate database
docker compose down -v
docker compose up -d postgres
# Wait for healthy status
docker compose up -d backend
```

### Frontend Can't Reach Backend

**Symptom:** API calls return 404 or CORS errors

**Diagnosis:**
```bash
# Check backend is running
curl http://localhost:8081/api/stations

# Check CORS configuration
docker compose exec backend cat /app/src/main/resources/application-dev.properties | grep cors

# Check network connectivity
docker compose exec frontend wget -O- http://backend:8081/api/stations
```

**Solutions:**
```bash
# Restart backend
docker compose restart backend

# Check backend logs for errors
docker compose logs backend | grep ERROR

# Verify backend health
curl http://localhost:8081/actuator/health
```

### Hot Reload Not Working

**Symptom:** Code changes not reflected in running application

**Backend Diagnosis:**
```bash
# Check volume is mounted
docker compose exec backend ls -la /app/src/main/java/com/radioawa/

# Verify Spring DevTools is active
docker compose logs backend | grep "LiveReload"
```

**Frontend Diagnosis:**
```bash
# Check volume is mounted
docker compose exec frontend ls -la /app/src/

# Verify Vite HMR is active
docker compose logs frontend | grep "hmr"
```

**Solutions:**
```bash
# Restart service
docker compose restart backend

# Rebuild if dependencies changed
docker compose up -d --build backend

# Check file permissions (Linux)
sudo chown -R $USER:$USER ./backend/src ./frontend/src
```

### Out of Disk Space

**Symptom:** Docker build fails with `no space left on device`

**Diagnosis:**
```bash
# Check Docker disk usage
docker system df

# Show detailed usage
docker system df -v
```

**Solutions:**
```bash
# Remove unused images (saves most space)
docker image prune -a

# Remove stopped containers
docker container prune

# Remove unused volumes (⚠️ careful!)
docker volume prune

# Nuclear option: remove everything
docker system prune -a --volumes

# Increase Docker disk size (Docker Desktop)
# Settings → Resources → Disk image size → Increase
```

### Permission Denied Errors

**Symptom:** Container fails with permission errors

**Common Causes:**
- Running Docker without sudo (Linux)
- Volume mount permission issues

**Solutions:**
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
# Log out and back in

# Fix file ownership
sudo chown -R $USER:$USER ./backend ./frontend

# Run with sudo (not recommended)
sudo docker compose up
```

### Container Keeps Restarting

**Symptom:** Container status shows `Restarting`

**Diagnosis:**
```bash
# View exit code and logs
docker compose ps
docker compose logs backend --tail=50
```

**Common Causes:**
- Application crash on startup
- Missing environment variables
- Database not ready

**Solutions:**
```bash
# Check for missing env vars
docker compose config

# Start services in order
docker compose up -d postgres
# Wait for healthy
docker compose up -d backend

# Disable restart to see error
# Edit docker-compose.yml: restart: "no"
docker compose up backend
```

---

## Performance Tuning

### Production JVM Options

Edit `docker-compose.prod.yml`:
```yaml
environment:
  JAVA_OPTS: >-
    -Xmx1g
    -Xms512m
    -XX:+UseG1GC
    -XX:MaxGCPauseMillis=200
    -XX:ParallelGCThreads=2
    -XX:ConcGCThreads=2
    -XX:InitiatingHeapOccupancyPercent=70
```

**Options Explained:**
- `-Xmx1g`: Max heap 1GB
- `-Xms512m`: Initial heap 512MB
- `+UseG1GC`: Use G1 garbage collector (low latency)
- `MaxGCPauseMillis=200`: Target max pause 200ms

### Database Connection Pool

Edit `backend/src/main/resources/application-prod.properties`:
```properties
# Connection pool tuning
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

**Formula for pool size:**
```
pool_size = (core_count * 2) + effective_spindle_count
```

For typical cloud instance (4 cores, SSD):
```
pool_size = (4 * 2) + 1 = 9-20 connections
```

### Nginx Configuration

The production frontend uses a custom Nginx configuration file (`frontend/nginx.conf`) that provides:
- **SPA Routing**: Serves index.html for all routes
- **API Proxy**: Forwards `/api` requests to backend service
- **Security Headers**: X-Frame-Options, X-Content-Type-Options, etc.
- **Gzip Compression**: Optimizes text-based responses
- **Static Asset Caching**: Aggressive caching for JS/CSS/images
- **Health Check Endpoint**: `/health` for monitoring

The configuration is automatically applied during the Docker build process.

### Nginx Caching

The `nginx.conf` file includes optimized caching settings:
```nginx
server {
    location /static/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico)$ {
        expires 30d;
        add_header Cache-Control "public, no-transform";
    }
}
```

---

## Security Best Practices

### Development Environment
1. Use `.env` files, never commit them
2. Default passwords acceptable for local dev
3. All ports can be exposed for debugging
4. Run containers with default user permissions

### Production Environment

**1. Strong Passwords**
```bash
# Generate strong password
openssl rand -base64 32

# Minimum 20 characters, mix of:
# - Uppercase letters
# - Lowercase letters
# - Numbers
# - Special characters
```

**2. SSL/TLS (Use Reverse Proxy)**
```yaml
# docker-compose.prod.yml
services:
  traefik:
    image: traefik:v2.10
    command:
      - "--providers.docker=true"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.letsencrypt.acme.tlschallenge=true"
      - "--certificatesresolvers.letsencrypt.acme.email=admin@radioawa.com"
    ports:
      - "80:80"
      - "443:443"
```

**3. Regular Updates**
```bash
# Weekly security updates
docker compose pull
docker compose build --pull
docker compose up -d --force-recreate
```

**4. Monitoring & Alerts**
```yaml
# docker-compose.prod.yml
services:
  backend:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

**5. Resource Limits**
```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 512M
```

**6. Read-Only Filesystem** (where possible)
```yaml
services:
  frontend:
    read_only: true
    tmpfs:
      - /tmp
      - /var/cache/nginx
```

**7. Docker Secrets** (production)
```bash
# Create secret
echo "my_db_password" | docker secret create db_password -

# Use in compose
services:
  postgres:
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
```

**8. Network Segmentation**
```yaml
# Separate networks for tiers
networks:
  frontend-tier:
  backend-tier:
  database-tier:

services:
  frontend:
    networks:
      - frontend-tier
  backend:
    networks:
      - frontend-tier
      - backend-tier
  postgres:
    networks:
      - backend-tier  # Not accessible from frontend
```

---

## Next Steps

### Immediate (After Setup)
- [ ] Access http://localhost:5171 and test both stations
- [ ] Test song ratings functionality
- [ ] Review logs: `docker compose logs -f`
- [ ] Bookmark this guide for reference

### Short-Term (First Week)
- [ ] Set up automated database backups (cron job)
- [ ] Configure monitoring (health check dashboard)
- [ ] Test disaster recovery (restore from backup)
- [ ] Review and tune resource limits

### Medium-Term (First Month)
- [ ] Set up CI/CD pipeline (GitHub Actions, GitLab CI)
- [ ] Configure SSL/TLS with Let's Encrypt
- [ ] Implement centralized logging (ELK, Loki, CloudWatch)
- [ ] Add Prometheus metrics and Grafana dashboards

### Long-Term (Production)
- [ ] Deploy to cloud (AWS ECS, GKE, Azure AKS)
- [ ] Set up auto-scaling based on metrics
- [ ] Implement blue-green or canary deployments
- [ ] Configure CDN for static assets
- [ ] Set up multi-region failover

### Learning Resources
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Specification](https://docs.docker.com/compose/compose-file/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

---

## Support

### Getting Help

**1. Check Logs First**
```bash
docker compose logs -f
docker compose logs backend
docker compose logs postgres
```

**2. Review Common Issues**
- See [Known Issues](#known-issues) section
- See [Troubleshooting](#troubleshooting) section

**3. Community Support**
- Check [GitHub Issues](https://github.com/yourusername/radioawa/issues)
- Search existing issues for similar problems
- Create new issue with logs and system info

**4. Documentation**
- [README.md](./README.md) - Project overview
- [SETUP.md](./SETUP.md) - Traditional setup
- [TECHNICAL-ARCHITECTURE.md](./TECHNICAL-ARCHITECTURE.md) - Architecture details

### Reporting Issues

When reporting Docker-related issues, include:

```bash
# System information
docker --version
docker compose version
uname -a

# Container status
docker compose ps

# Recent logs
docker compose logs --tail=100 > logs.txt

# Resource usage
docker stats --no-stream
```

---

**Document Version**: 1.0
**Last Updated**: 2024-12-19
**Author**: RadioAwa Team
**License**: MIT

---

*This guide is maintained alongside the RadioAwa project. For updates and contributions, see the [GitHub repository](https://github.com/yourusername/radioawa).*
