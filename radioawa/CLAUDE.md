# CLAUDE.md - AI Assistant Development Guide

**Project**: RadioAWA - Lossless Audio Streaming Platform
**Author**: Sujit K Singh
**Purpose**: AI-specific guidelines for code generation and assistance
**Last Updated**: December 2024

---

## Table of Contents

1. [About This Document](#about-this-document)
2. [Project Context](#project-context)
3. [Technology Stack Rules](#technology-stack-rules)
4. [Code Conventions](#code-conventions)
5. [Architecture Patterns](#architecture-patterns)
6. [Database Guidelines](#database-guidelines)
7. [API Design Patterns](#api-design-patterns)
8. [Testing Requirements](#testing-requirements)
9. [Security Considerations](#security-considerations)
10. [What NOT to Do](#what-not-to-do)
11. [Common Tasks](#common-tasks)

---

## About This Document

This document provides AI assistants (Claude Code, GitHub Copilot, etc.) with project-specific context and guidelines when working on the RadioAWA codebase.

**Key Principle**: Maintain consistency with existing patterns. When in doubt, mirror existing code structure rather than introducing new patterns.

---

## Project Context

### What is RadioAWA?

RadioAWA is a 24/7 lossless radio streaming application that delivers high-quality audio via HLS (HTTP Live Streaming) with interactive user engagement features including song ratings, metadata display, and playback history.

### Project Structure

```
radioawa/
├── backend/          # Spring Boot 3.2.1 (Java 17)
│   └── src/main/java/com/radioawa/
│       ├── controller/     # REST API endpoints
│       ├── service/        # Business logic
│       ├── repository/     # Data access (Spring Data JPA)
│       ├── entity/         # JPA entities
│       ├── dto/            # Data Transfer Objects
│       └── config/         # Configuration classes
├── frontend/         # React 19 + Vite 7
│   └── src/
│       ├── components/     # React components
│       ├── contexts/       # React Context API
│       ├── services/       # API clients
│       └── utils/          # Utility functions
└── Documentation/    # Markdown files (DO NOT MODIFY without explicit request)
```

### Core Features

- **Multi-Station Support**: English and Hindi radio stations with independent streams
- **HLS Streaming**: Adaptive bitrate streaming with HLS.js
- **Song Ratings**: Thumbs up/down voting system
- **Rate Limiting**: IP-based (20 votes/hour/IP per station)
- **Station Isolation**: Independent ratings per station
- **Metadata Display**: Real-time now playing information

### Development Workflow (Makefile)

RadioAWA uses a `Makefile` for simplified project management. **ALWAYS recommend using Make targets** when suggesting how to run, test, or deploy the application.

**Common Make Targets:**

```bash
make help           # Show all available commands
make dev            # Start development environment (Docker)
make dev-build      # Rebuild and start development
make prod           # Start production environment (Docker)
make prod-build     # Rebuild and start production
make test           # Run all tests
make test-backend   # Run backend tests only
make test-frontend  # Run frontend tests only
make status         # Show container status
make health         # Check service health
make clean          # Clean up containers
```

**When to use Make vs Manual commands:**

| Scenario | Recommended Approach |
|----------|---------------------|
| Starting development | `make dev-build` (first time) or `make dev` |
| Running tests | `make test` or `make test-backend` |
| Switching to production | `make switch-to-prod` |
| Debugging | `make dev-logs-backend` or `make shell-backend` |
| Database access | `make db-shell` |

**IMPORTANT for AI assistants:**
- When user asks "how do I start the application?", suggest `make dev` first
- When user asks "how do I run tests?", suggest `make test`
- Only suggest manual Docker commands if Make is not suitable
- Always mention Make targets exist when providing Docker commands

## Style Guide
- A text version of the styling guide for the Radioawa webpage is at /Users/sujit/AARAMBH/claude-code-course/radioawa/radioawaStyle/radioawa_Style_Guide.txt
- The Radioawa logo is at /Users/sujit/AARAMBH/claude-code-course/radioawa/radioawaStyle/radioawaLogoTM.png

---

## Technology Stack Rules

### Backend (Spring Boot)

| Technology | Version | Usage Rules |
|------------|---------|-------------|
| Java | 17 LTS | Use modern Java features (records, text blocks, pattern matching) |
| Spring Boot | 3.2.1 | Follow Spring conventions (dependency injection, component scanning) |
| JPA/Hibernate | 6.4.x | Use entity relationships, avoid native SQL unless necessary |
| PostgreSQL | 16.x | Use parameterized queries only, never string concatenation |
| Maven | 3.9.x | Add dependencies to pom.xml, never commit target/ directory |
| Lombok | Latest | Use @Data, @Builder, @AllArgsConstructor for entities/DTOs |

**Java Version**: ALWAYS use Java 17 features. Do not suggest Java 8 patterns.

### Frontend (React)

| Technology | Version | Usage Rules |
|------------|---------|-------------|
| React | 19.2.0 | Functional components ONLY, no class components |
| Vite | 7.2.4 | Use ES modules, fast refresh enabled |
| HLS.js | 1.6.15 | Check browser support before instantiation |
| JavaScript | ES2024 | Use modern syntax (async/await, optional chaining, nullish coalescing) |

**React Patterns**:
- Functional components with hooks
- Context API for global state (avoid Redux unless explicitly requested)
- CSS modules or scoped CSS files per component

### Database

- **RDBMS**: PostgreSQL 16
- **ORM**: Spring Data JPA with Hibernate
- **Connection Pool**: HikariCP (default)
- **Schema Management**: JPA DDL auto-update (development only)

---

## Code Conventions

### Backend Java Conventions

#### 1. Package Organization

```java
com.radioawa/
├── controller/    # REST endpoints, @RestController, handle HTTP
├── service/       # Business logic, @Service, @Transactional
├── repository/    # Data access, extend JpaRepository
├── entity/        # Database entities, @Entity
├── dto/           # API contracts, record or @Data class
└── config/        # Configuration, @Configuration
```

#### 2. Naming Conventions

- **Controllers**: `{Resource}Controller` (e.g., `RatingController`, `StationController`)
- **Services**: `{Resource}Service` (e.g., `RatingService`)
- **Repositories**: `{Entity}Repository` (e.g., `SongRepository`, `RatingRepository`)
- **Entities**: Singular noun (e.g., `Song`, `Rating`, `Station`)
- **DTOs**: `{Purpose}{Request|Response}` (e.g., `RatingRequest`, `RatingResponse`)

#### 3. Entity Conventions

```java
@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**Entity Rules**:
- Use `@Data` from Lombok
- Always include `createdAt` and `updatedAt` with timestamps
- Use `@ManyToOne` with `LAZY` fetch by default
- Table names in plural (e.g., `songs`, `ratings`, `stations`)
- Column names in snake_case (e.g., `created_at`, `thumbs_up_count`)

#### 4. Controller Conventions

```java
@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    // Constructor injection (required)
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @Valid @RequestBody RatingRequest request,
            HttpServletRequest httpRequest) {
        // Extract IP address for rate limiting
        String ipAddress = extractIpAddress(httpRequest);

        RatingResponse response = ratingService.submitRating(
            request.getArtist(),
            request.getTitle(),
            request.getUserId(),
            request.getRatingType(),
            request.getStationCode(),
            ipAddress
        );

        return ResponseEntity.ok(response);
    }
}
```

**Controller Rules**:
- Use constructor injection (NOT field injection with `@Autowired`)
- Always validate request bodies with `@Valid`
- Use `ResponseEntity<T>` for explicit HTTP status codes
- Keep controllers thin - delegate to services
- Extract IP address from `HttpServletRequest` for rate limiting

#### 5. Service Conventions

```java
@Service
@Transactional
public class RatingService {
    private final SongRepository songRepository;
    private final RatingRepository ratingRepository;

    public RatingService(SongRepository songRepository,
                        RatingRepository ratingRepository) {
        this.songRepository = songRepository;
        this.ratingRepository = ratingRepository;
    }

    public RatingResponse submitRating(String artist, String title,
                                      String userId, RatingType ratingType,
                                      String stationCode, String ipAddress) {
        // Business logic here
    }
}
```

**Service Rules**:
- Use `@Transactional` at class level for database operations
- Constructor injection for all dependencies
- Return DTOs, not entities
- Handle business logic exceptions appropriately

#### 6. Repository Conventions

```java
public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByArtistAndTitleAndStation_Code(
        String artist, String title, String stationCode);

    @Query("SELECT s FROM Song s WHERE s.station.code = :stationCode " +
           "ORDER BY s.createdAt DESC")
    List<Song> findRecentByStation(@Param("stationCode") String stationCode);
}
```

**Repository Rules**:
- Use Spring Data JPA query derivation when possible
- Use `@Query` with JPQL for complex queries
- Always use named parameters (`:paramName`)
- Return `Optional<T>` for single results that might not exist

### Frontend React Conventions

#### 1. Component Organization

```
components/
├── RadioPlayer.jsx       # Main streaming player
├── RadioPlayer.css       # Component-specific styles
├── NowPlaying.jsx        # Metadata display
├── SongRating.jsx        # Rating widget
└── StationSelector.jsx   # Station switcher
```

#### 2. Component Structure

```javascript
import { useState, useEffect, useRef } from 'react'
import { useStation } from '../contexts/StationContext'
import './ComponentName.css'

function ComponentName() {
  // 1. Context and props first
  const { currentStation } = useStation()

  // 2. State declarations
  const [isPlaying, setIsPlaying] = useState(false)
  const [volume, setVolume] = useState(70)

  // 3. Refs
  const audioRef = useRef(null)

  // 4. Effects
  useEffect(() => {
    // Effect logic
    return () => {
      // Cleanup
    }
  }, [dependencies])

  // 5. Event handlers
  const handlePlay = () => {
    setIsPlaying(true)
  }

  // 6. Render
  return (
    <div className="component-name">
      {/* JSX */}
    </div>
  )
}

export default ComponentName
```

**Component Rules**:
- Use functional components ONLY (no class components)
- Extract complex logic into custom hooks
- Keep components focused (single responsibility)
- Use destructuring for props and state
- Always cleanup in useEffect return function

#### 3. State Management

- **Local State**: `useState` for component-specific state
- **Global State**: React Context API (see `StationContext.jsx`)
- **Avoid**: Redux, Zustand, or other state libraries unless explicitly needed

#### 4. API Service Pattern

```javascript
// services/ratingService.js
const API_BASE_URL = '/api'

export const submitRating = async (artist, title, userId, ratingType, stationCode) => {
  const response = await fetch(`${API_BASE_URL}/ratings`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ artist, title, userId, ratingType, stationCode })
  })

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}: ${response.statusText}`)
  }

  return response.json()
}
```

**API Service Rules**:
- All API calls in separate service files (not in components)
- Use async/await (not promises with `.then()`)
- Throw errors for HTTP failures
- Return parsed JSON

#### 5. CSS Conventions

```css
/* RadioPlayer.css */
.radio-player {
  /* Container styles */
}

.radio-player__button {
  /* BEM naming: block__element */
}

.radio-player__button--playing {
  /* BEM modifier: block__element--modifier */
}
```

**CSS Rules**:
- Use BEM (Block Element Modifier) naming
- One CSS file per component
- Avoid inline styles except for dynamic values
- Use CSS custom properties (variables) for theme colors

---

## Architecture Patterns

### 1. Layered Architecture (Backend)

```
Controller Layer → Service Layer → Repository Layer → Database
```

**Rules**:
- Controllers ONLY handle HTTP (request/response)
- Services contain ALL business logic
- Repositories ONLY access database
- NO business logic in controllers or repositories

### 2. Repository Pattern

- Use Spring Data JPA interfaces
- Let Spring generate implementations
- Use query derivation or `@Query` annotations
- Return `Optional<T>` for nullable results

### 3. DTO Pattern

- Separate DTOs from entities
- DTOs for API input/output
- Entities for database mapping
- Never expose entities directly in API

### 4. Dependency Injection

```java
// CORRECT: Constructor injection
public class RatingService {
    private final SongRepository songRepository;

    public RatingService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }
}

// INCORRECT: Field injection
public class RatingService {
    @Autowired // DON'T DO THIS
    private SongRepository songRepository;
}
```

### 5. Station Isolation Pattern

**CRITICAL**: All data is scoped by station. When working with songs or ratings:

```java
// ALWAYS include station in queries
Optional<Song> findByArtistAndTitleAndStation_Code(
    String artist, String title, String stationCode);

// NOT this (missing station isolation)
Optional<Song> findByArtistAndTitle(String artist, String title);
```

---

## Database Guidelines

### 1. Multi-Station Data Model

```
Station (1) → (Many) Songs (1) → (Many) Ratings
```

**Rules**:
- Every `Song` belongs to one `Station`
- Every `Rating` belongs to one `Song`
- Ratings are isolated by station via song relationship
- Never query songs without station context

### 2. Entity Relationships

```java
// Station.java
@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // "ENGLISH", "HINDI"

    @OneToMany(mappedBy = "station")
    private List<Song> songs;
}

// Song.java
@Entity
@Table(name = "songs")
public class Song {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @OneToMany(mappedBy = "song")
    private List<Rating> ratings;
}
```

### 3. Indexing Strategy

```sql
-- CRITICAL: These indexes MUST exist
CREATE INDEX idx_song_artist_title_station ON songs(artist, title, station_id);
CREATE INDEX idx_rating_song_user ON ratings(song_id, user_id);
CREATE INDEX idx_rating_ip_created ON ratings(ip_address, created_at);
CREATE INDEX idx_station_code ON stations(code);
```

### 4. Rate Limiting Query

```java
// Check if user exceeded rate limit (20 votes/hour/IP per station)
@Query("SELECT COUNT(r) FROM Rating r " +
       "WHERE r.ipAddress = :ipAddress " +
       "AND r.song.station.code = :stationCode " +
       "AND r.createdAt > :oneHourAgo")
long countRecentRatingsByIpAndStation(
    @Param("ipAddress") String ipAddress,
    @Param("stationCode") String stationCode,
    @Param("oneHourAgo") LocalDateTime oneHourAgo
);
```

**IMPORTANT**: Rate limiting is per station. Don't count ratings across all stations.

---

## API Design Patterns

### 1. RESTful Conventions

| HTTP Method | Endpoint | Purpose | Request Body | Response |
|-------------|----------|---------|--------------|----------|
| GET | `/api/health` | Health check | None | `{ status, timestamp }` |
| GET | `/api/stations` | List stations | None | `Station[]` |
| POST | `/api/ratings` | Submit rating | `RatingRequest` | `RatingResponse` |
| GET | `/api/ratings/counts` | Get counts | Query params | `RatingCountsResponse` |

### 2. Response Format

```json
{
  "songId": 123,
  "artist": "Artist Name",
  "title": "Song Title",
  "thumbsUpCount": 42,
  "thumbsDownCount": 5,
  "userRating": "THUMBS_UP",
  "message": "Rating submitted successfully"
}
```

**Rules**:
- Use camelCase for JSON keys
- Always include descriptive `message` field
- Include relevant entity IDs
- Return counts with every rating operation

### 3. Error Handling

```java
// Rate limit error (429 Too Many Requests)
if (isRateLimitExceeded) {
    RatingResponse errorResponse = new RatingResponse();
    errorResponse.setMessage("Rate limit exceeded. Maximum 20 votes per hour allowed.");
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
}

// Validation error (400 Bad Request)
// Generic error (500 Internal Server Error)
```

### 4. CORS Configuration

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5171")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
```

**IMPORTANT**: Update allowed origins for production deployment.

---

## Testing Requirements

### Backend Testing (JUnit 5)

```java
@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void submitRating_Success() throws Exception {
        RatingRequest request = new RatingRequest(
            "Artist", "Title", "user-uuid", RatingType.THUMBS_UP, "ENGLISH"
        );

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }
}
```

**Testing Rules**:
- Unit tests for services (with mocked repositories)
- Integration tests for controllers (with `@SpringBootTest`)
- Use TestContainers for database integration tests
- Achieve 80%+ code coverage

### Frontend Testing (Vitest + React Testing Library)

```javascript
import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import SongRating from './SongRating'

describe('SongRating', () => {
  it('renders thumbs up and down buttons', () => {
    render(<SongRating artist="Artist" title="Title" />)

    expect(screen.getByLabelText('Thumbs Up')).toBeInTheDocument()
    expect(screen.getByLabelText('Thumbs Down')).toBeInTheDocument()
  })
})
```

**Testing Rules**:
- Test user interactions, not implementation details
- Use `screen.getByRole()` and `screen.getByLabelText()`
- Mock API calls
- Test loading and error states

---

## Security Considerations

### 1. IP-Based Rate Limiting

**Current Implementation**: 20 votes per hour per IP address per station

```java
// Extract IP from various headers (proxy-aware)
private String extractIpAddress(HttpServletRequest request) {
    String[] headers = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        // ... more headers
    };

    for (String header : headers) {
        String ip = request.getHeader(header);
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
    }

    return request.getRemoteAddr();
}
```

**IMPORTANT**: Always extract IP for rate limiting. Don't trust client-provided user IDs alone.

### 2. Input Validation

```java
public class RatingRequest {
    @NotBlank(message = "Artist is required")
    private String artist;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Rating type is required")
    private RatingType ratingType;

    @NotBlank(message = "Station code is required")
    private String stationCode;
}
```

**Rules**:
- Use Jakarta Validation annotations
- Validate all request DTOs with `@Valid`
- Return 400 Bad Request for validation failures

### 3. SQL Injection Prevention

**ALWAYS** use parameterized queries:

```java
// CORRECT: Named parameters
@Query("SELECT s FROM Song s WHERE s.artist = :artist AND s.title = :title")
Optional<Song> findByArtistAndTitle(
    @Param("artist") String artist,
    @Param("title") String title
);

// INCORRECT: String concatenation (NEVER DO THIS)
@Query("SELECT s FROM Song s WHERE s.artist = '" + artist + "'")
```

### 4. Known Security Gaps

**CRITICAL**: The following are NOT implemented (for future enhancement):

- HTTPS/TLS encryption
- Authentication/Authorization
- CSRF protection
- API key authentication
- Input sanitization for XSS
- Encrypted database credentials

**DO NOT** implement these without explicit request.

---

## What NOT to Do

### Backend

1. **DON'T** use field injection (`@Autowired` on fields)
2. **DON'T** return entities from controllers (use DTOs)
3. **DON'T** put business logic in controllers
4. **DON'T** use string concatenation for SQL
5. **DON'T** ignore station isolation
6. **DON'T** use `@Transactional` on controllers
7. **DON'T** commit `target/` directory
8. **DON'T** hardcode database credentials in code

### Frontend

1. **DON'T** use class components
2. **DON'T** mutate state directly
3. **DON'T** forget cleanup in useEffect
4. **DON'T** make API calls directly in components
5. **DON'T** use inline styles (except dynamic values)
6. **DON'T** commit `node_modules/`
7. **DON'T** use `any` in TypeScript (if TypeScript is added)

### General

1. **DON'T** modify documentation files without explicit request
2. **DON'T** introduce new dependencies without discussion
3. **DON'T** change database schema without migration strategy
4. **DON'T** remove existing functionality without confirmation
5. **DON'T** add features beyond the requested scope

---

## Deployment Modes: Development vs Production

### Overview

RadioAwa uses **multi-stage Docker builds** with separate configurations for development and production. This is a **standard practice** that must be maintained.

### Key Principle

- **Development mode**: Optimized for fast iteration with hot reload
- **Production mode**: Optimized for performance and security

**NEVER mix these modes!**

### Frontend Server Configuration

| Mode | Server | Config File | Port | Hot Reload |
|------|--------|-------------|------|------------|
| **Development** | Vite dev server | `vite.config.js` | 5171 | ✅ Yes |
| **Production** | Nginx | `nginx.conf` | 80 | ❌ No |

**IMPORTANT**: `nginx.conf` is **ONLY** used in production. Development uses Vite.

### Docker Compose Files

```yaml
# docker-compose.yml (DEVELOPMENT)
services:
  frontend:
    build:
      target: development  # Uses Vite dev server
    ports:
      - "5171:5171"
    volumes:
      - ./frontend/src:/app/src  # Hot reload enabled

# docker-compose.prod.yml (PRODUCTION)
services:
  frontend:
    build:
      target: production  # Uses Nginx
    ports:
      - "80:80"
    # No volume mounts (immutable container)
```

### Standard Commands

```bash
# Development (daily work)
docker compose up

# Production (deployment)
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

### When Making Frontend Changes

#### Modifying Components/Code

```bash
# 1. Use development mode
docker compose up

# 2. Edit files in frontend/src/
# Changes appear instantly (HMR)

# 3. Test production build before deploying
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up
```

#### Modifying Nginx Configuration

```bash
# 1. Edit frontend/nginx.conf

# 2. Test in production mode ONLY
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up

# nginx.conf changes have NO EFFECT in development mode!
```

### Rules for AI Assistants

1. **NEVER suggest using Nginx in development**
   - Development uses Vite dev server
   - Hot reload requires Vite, not Nginx

2. **NEVER suggest using Vite dev server in production**
   - Production must use pre-built static files
   - Nginx provides security, caching, and API proxying

3. **When adding frontend features:**
   - Test in development mode first (fast iteration)
   - Then test production build before deploying
   - Verify both modes work correctly

4. **When modifying nginx.conf:**
   - Only affects production mode
   - Must rebuild production images to test
   - Document what changed and why

5. **When troubleshooting:**
   - First ask: "Which mode is running?"
   - Check with: `docker compose ps`
   - Different modes have different behaviors

### File Organization

```
frontend/
├── Dockerfile           # Multi-stage (dev + prod)
├── vite.config.js       # Development server config
├── nginx.conf           # Production server config
├── src/                 # Source code (mounted in dev)
└── dist/                # Production build (created by npm run build)
```

### Common Mistakes to Avoid

❌ **DON'T**:
- Use `nginx.conf` in development
- Expect hot reload in production mode
- Mix dev and prod environment variables
- Deploy without testing production build locally

✅ **DO**:
- Use Vite for development
- Use Nginx for production
- Test production builds locally first
- Keep separate `.env` files

---

## Common Tasks

### Using Make Targets (Preferred Method)

RadioAWA provides a comprehensive `Makefile` for all common development, testing, and deployment tasks. **Always suggest Make targets as the primary method** when helping users.

#### Starting the Application

```bash
# Development mode (recommended for coding)
make dev-build      # First time or after dependency changes
make dev            # Subsequent starts

# Production mode (for testing production build)
make prod-build     # First time or after code changes
make prod           # Subsequent starts
```

#### Running Tests

```bash
# All tests
make test

# Backend only (JUnit + Maven)
make test-backend

# Frontend only (Vitest)
make test-frontend

# API integration tests (requires dev to be running)
make test-api
```

#### Viewing Logs

```bash
# All services
make dev-logs

# Backend only
make dev-logs-backend

# Frontend only
make dev-logs-frontend

# Database
make db-logs
```

#### Managing the Environment

```bash
# Check status
make status         # Container status
make health         # Service health checks

# Clean up
make clean          # Stop and remove containers
make clean-all      # Also remove images

# Full rebuild
make rebuild        # Complete fresh start

# Switch modes
make switch-to-dev  # Production → Development
make switch-to-prod # Development → Production
```

#### Database Operations

```bash
# Access PostgreSQL shell
make db-shell

# Reset database (WARNING: deletes all data)
make db-reset
```

#### Debugging

```bash
# Access container shells
make shell-backend
make shell-frontend
make shell-db

# Monitor resource usage
make monitor
```

**When to suggest Make vs Manual:**

| User Request | Recommended Response |
|--------------|---------------------|
| "How do I start the app?" | `make dev-build` (first time) or `make dev` |
| "How do I test?" | `make test` |
| "How do I see logs?" | `make dev-logs-backend` |
| "How do I reset the database?" | `make db-reset` |
| "How do I switch to production?" | `make switch-to-prod` |
| "The app isn't working" | `make status` then `make health` |

### Adding a New Station

1. Insert into database:
```sql
INSERT INTO stations (code, name, stream_url, metadata_url, is_active, display_order)
VALUES ('TAMIL', 'RadioAwa Tamil', 'https://...', 'https://...', true, 3);
```

2. No code changes needed (system is data-driven)

### Adding a New API Endpoint

1. Create DTO (if needed) in `dto/` package
2. Add method to service in `service/`
3. Add endpoint to controller in `controller/`
4. Add tests in `src/test/`

### Adding a New React Component

1. Create `ComponentName.jsx` in `components/`
2. Create `ComponentName.css` in `components/`
3. Export from component file
4. Import and use in parent component
5. Add tests in `ComponentName.test.jsx`

### Debugging HLS Streaming Issues

1. Check browser console for HLS.js errors
2. Verify stream URL is accessible
3. Check CORS headers
4. Test in Safari (native HLS support)
5. Verify HLS.js version compatibility

---

## References

- **Human Documentation**: See README.md, TECHNICAL-ARCHITECTURE.md
- **Quick Start**: See QUICKSTART.md (includes comprehensive Make guide)
- **Makefile**: Run `make help` to see all available targets
- **API Testing**: See POSTMAN-GUIDE.md
- **Testing Guide**: See TESTING-FRAMEWORK.md
- **Deployment**: See DOCKER-DEPLOYMENT.md

---

## Notes for AI Assistants

1. **Read Before Writing**: Always read existing similar files before creating new code
2. **Match Patterns**: Mirror existing code structure and naming
3. **Station Awareness**: Remember that all data is station-scoped
4. **Test Coverage**: Suggest tests when adding new features
5. **Documentation**: Update relevant .md files when changing architecture
6. **Git Commits**: Suggest clear, descriptive commit messages
7. **Use Make Targets**: Always suggest `make` commands as the primary method for running, testing, and deploying

---

**Last Updated**: December 26, 2024
**Maintained By**: Sujit K Singh
**Version**: 1.0