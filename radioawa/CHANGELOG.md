# Changelog

All notable changes to the RadioAwa project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed - Bug Fixes and Enhancements (2025-12-20)

#### Critical Bug Fixes (8 issues resolved)
1. **Hindi Station Metadata Not Loading** - Fixed metadata URL configuration to use backend proxy endpoint
2. **Failed to Load Ratings (Both Stations)** - Fixed CORS and proxy configuration for Docker environment
3. **Hindi Station Album Artwork Missing** - Added album art URLs with placehold.co service
4. **English Station Album Artwork Missing** - Added 3-level fallback chain for dynamic artwork generation
5. **Audio Stream Mismatch** - Added demo notice banner to inform users about stream type
6. **Hindi Station Updated to Vividh Bharati** - Replaced test stream with real All India Radio Vividh Bharati
7. **Station Selector Button Label Bug** - Fixed Hindi button showing "English" instead of "हिंदी"
8. **Vividh Bharati Stream Not Working** - Updated to working BitGravity CDN stream URL

#### Enhancements
- **Hindi Station** now plays real Vividh Bharati radio from All India Radio
- **Album Artwork** working for both English and Hindi stations with colorful placeholders
- **Station Selector** displays correct labels: "English" and "हिंदी"
- **Metadata System** shows simulated popular Hindi classics with representative playlist
- **Demo Notice Banner** informs users about live Vividh Bharati stream

#### Documentation
- Added `BUG-FIXES.md` - Comprehensive documentation of all 8 bugs fixed with root cause analysis
- Added `HINDI-STREAM-OPTIONS.md` - Guide for finding and testing Hindi radio stream URLs

### Added - Docker Support (2025-12-19)

#### Docker Containerization
- **Multi-stage Dockerfiles** for both backend and frontend services
  - Backend: Development stage with hot reload + Production stage with optimized JRE
  - Frontend: Development stage with Vite HMR + Production stage with Nginx
  - Image size optimizations: Production images are 70-90% smaller than dev images

- **Docker Compose configurations**
  - `docker compose.yml` - Full-featured development environment with hot reload
  - `docker compose.prod.yml` - Production-optimized deployment with security hardening
  - Health checks for all services (postgres, backend, frontend)
  - Automatic database initialization with SQL migration scripts

- **Environment configuration files**
  - `.env.docker.dev` - Development environment variables template
  - `.env.docker.prod` - Production environment variables template
  - `backend/src/main/resources/application-dev.properties` - Dev-specific Spring configuration
  - `backend/src/main/resources/application-prod.properties` - Prod-specific Spring configuration

- **Docker utilities**
  - `docker-setup.sh` - Interactive setup script for easy deployment
  - `.dockerignore` files for backend, frontend, and project root
  - Comprehensive `.gitignore` coverage for Docker artifacts

- **Documentation**
  - `DOCKER-DEPLOYMENT.md` - Complete 700+ line Docker deployment guide including:
    - Purpose and benefits of containerization
    - Architecture diagrams and explanations
    - Development and production deployment instructions
    - Container images breakdown
    - Networking and communication flows
    - Data persistence strategies
    - Security best practices
    - Troubleshooting guide
    - Performance tuning tips
  - Updated `README.md` with Docker quick start section
  - Updated `SETUP.md` with Docker references

#### Backend Enhancements
- Added **Spring Boot Actuator** dependency for health monitoring
  - Health endpoint: `/actuator/health`
  - Production health checks configured

- Multi-environment Spring profiles
  - Dev profile with verbose logging and CORS for all origins
  - Prod profile with optimized connection pooling and restricted logging

#### Features & Benefits
- **Zero-configuration setup**: Run entire stack with `docker compose up`
- **Environment consistency**: Identical environments across dev, test, and production
- **Isolated dependencies**: No need to install Java 17, Node.js 20, or PostgreSQL 16
- **Hot reload support**: Code changes automatically reflected in development mode
- **Production-ready**: Optimized builds, security hardening, health monitoring
- **Easy scaling**: Horizontal scaling ready for cloud deployments
- **Volume persistence**: Database data and caches persist across container restarts
- **Network isolation**: Services communicate via dedicated Docker network

#### Security
- Production containers run as non-root users
- Database and backend not exposed externally in production
- Environment variable management with .env files (git-ignored)
- Separate dev and prod configurations
- Health checks and graceful shutdown configured

#### Developer Experience
- Single-command startup: `docker compose up`
- Interactive setup script: `./docker-setup.sh`
- Fast rebuilds with Docker layer caching
- Source code mounted as volumes for instant feedback
- Comprehensive troubleshooting documentation

---

## [0.0.1] - 2024-12-18

### Added - Testing Framework
- JUnit 5 and Mockito for backend unit testing
- Vitest for frontend component testing
- Integration tests with TestContainers
- Code coverage reporting (JaCoCo for backend, Vitest coverage for frontend)

### Added - Multi-Station Feature
- Support for multiple radio stations (English and Hindi)
- Station switching capability
- Independent song ratings per station
- IP-based rate limiting per station (20 votes/hour/IP)

### Added - Core Features
- HLS audio streaming with 24-bit / 48 kHz quality
- React 19 frontend with Vite 7
- Spring Boot 3.2.1 backend
- PostgreSQL 16 database
- Song rating system (thumbs up/down)
- Now playing display with album artwork
- Recently played history
- Cross-browser support (Chrome, Firefox, Safari, Edge)
- CORS configuration for local development
- Automated startup/shutdown scripts

### Documentation
- TECHNICAL-ARCHITECTURE.md
- TESTING-FRAMEWORK.md
- MULTI-STATION-FEATURE.md
- HINDI-STATION-SETUP.md
- IP-TRACKING-IMPLEMENTATION.md
- POSTMAN-GUIDE.md
- QUICKSTART.md
- SETUP.md
- DEPLOYMENT.md

---

## Release Notes

### Docker Support Highlights

The Docker containerization feature represents a major milestone for RadioAwa, making it:
- **Easier to develop**: New contributors can start coding in minutes
- **Easier to deploy**: Production deployments simplified to a few commands
- **More reliable**: Consistent environments eliminate "works on my machine" issues
- **Cloud-ready**: Deploy to AWS, GCP, Azure, or any Kubernetes cluster
- **More secure**: Production containers follow security best practices

This release transforms RadioAwa from a local-only development project into a production-ready, cloud-native application.

---

[Unreleased]: https://github.com/yourusername/radioawa/compare/v0.0.1...HEAD
[0.0.1]: https://github.com/yourusername/radioawa/releases/tag/v0.0.1
