# radioawa - Technical Architecture Document

**Version:** 0.0.1-SNAPSHOT
**Last Updated:** December 2024
**Document Type:** Technical Architecture & Design

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Product Overview](#product-overview)
3. [Technology Stack](#technology-stack)
4. [System Architecture](#system-architecture)
5. [Component Design](#component-design)
6. [Design Patterns & Principles](#design-patterns--principles)
7. [Data Architecture](#data-architecture)
8. [API Design](#api-design)
9. [Security Architecture](#security-architecture)
10. [Performance & Scalability](#performance--scalability)
11. [Operational Architecture](#operational-architecture)
12. [Future Enhancements](#future-enhancements)

---

## Executive Summary

**radioawa** is a 24/7 lossless radio streaming application that combines real-time audio streaming with interactive user engagement features. The system enables listeners to stream high-quality audio content via HLS (HTTP Live Streaming) protocol while providing real-time metadata display, song ratings, and playback history.

### Key Features
- **Live HLS Audio Streaming** - Lossless quality audio delivery
- **Real-time Metadata Display** - Now Playing information with album artwork
- **Interactive Rating System** - Thumbs up/down voting with abuse prevention
- **Recently Played History** - Track listening history
- **Responsive Design** - Mobile-first UI with modern aesthetics

### Business Value
- Engages listeners through interactive features
- Provides valuable feedback on music preferences
- Scales to support growing listener base
- Low operational overhead for radio stations

---

## Product Overview

### What is radioawa?

radioawa is a web-based radio streaming platform designed for independent radio stations and content creators who want to deliver high-quality audio content with listener engagement features. The platform bridges the gap between traditional radio broadcasting and modern interactive web applications.

### Core Capabilities

1. **Streaming Engine**
   - HLS-based adaptive bitrate streaming
   - Buffer management and error recovery
   - Cross-browser compatibility

2. **Metadata Management**
   - Real-time song information updates
   - Album artwork fetching and caching
   - Artist and title tracking

3. **User Engagement**
   - Anonymous user identification
   - Song rating system (thumbs up/down)
   - Vote tracking and aggregation

4. **Abuse Prevention**
   - IP-based rate limiting
   - Duplicate vote prevention
   - Hybrid user identification (UUID + IP)

---

## Technology Stack

### Backend Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Runtime** | Java | 17 LTS | Application runtime environment |
| **Framework** | Spring Boot | 3.2.1 | Backend application framework |
| **Web Server** | Apache Tomcat | 10.1.x (embedded) | Servlet container and HTTP server |
| **ORM** | Hibernate (via JPA) | 6.4.x | Object-relational mapping |
| **Database** | PostgreSQL | 16.x | Relational database management |
| **Build Tool** | Apache Maven | 3.9.x | Dependency management and build automation |
| **Validation** | Jakarta Validation | 3.0.x | Request payload validation |

#### Spring Boot Dependencies

```xml
spring-boot-starter-web      → REST API, Embedded Tomcat
spring-boot-starter-data-jpa → Database access layer
spring-boot-starter-validation → Input validation
spring-boot-devtools         → Hot reload during development
postgresql                   → PostgreSQL JDBC driver
```

### Frontend Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Framework** | React | 19.2.0 | UI component library |
| **Build Tool** | Vite | 7.2.4 | Fast development server and bundler |
| **Streaming** | HLS.js | 1.6.15 | HLS video/audio player library |
| **Language** | JavaScript (ES6+) | ES2024 | Programming language |
| **Package Manager** | npm | 10.x | Dependency management |

#### Frontend Dependencies

```json
react          → Core UI framework
react-dom      → React DOM renderer
hls.js         → HLS streaming protocol implementation
vite           → Build tool with HMR
eslint         → Code quality and linting
```

### Database Layer

| Aspect | Technology | Purpose |
|--------|-----------|---------|
| **RDBMS** | PostgreSQL 16 | Primary data store |
| **Connection Pool** | HikariCP (default) | Database connection management |
| **Migration** | JPA DDL Auto | Schema synchronization (dev mode) |
| **Query Language** | JPQL + Native SQL | Data access queries |

### Infrastructure & DevOps

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Version Control** | Git | Source code management |
| **Package Registry** | Maven Central, npm Registry | Dependency hosting |
| **Process Management** | Shell Scripts | Start/stop/status management |
| **Database Tools** | psql, custom db-cli.sh | Database administration |

### Testing Framework

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Backend Testing** | JUnit 5 | Included in spring-boot-starter-test | Unit test framework |
| **Backend Mocking** | Mockito | Included in spring-boot-starter-test | Mock object creation |
| **Backend Assertions** | AssertJ | Latest | Fluent assertion library |
| **Backend Coverage** | JaCoCo | 0.8.10 | Code coverage measurement |
| **DB Testing** | TestContainers | 1.19.3 | Docker-based integration testing |
| **Frontend Testing** | Vitest | 1.0.4 | Fast unit test runner for Vite |
| **Frontend Components** | React Testing Library | 14.1.2 | Component testing utilities |
| **Frontend Interactions** | @testing-library/user-event | 14.5.1 | User interaction simulation |

**Test Execution**:
- Backend: `mvn test` (JUnit 5 via Maven Surefire)
- Frontend: `npm run test` (Vitest)

**Coverage Goals**:
- Overall: 80%+
- Backend: JaCoCo report at `target/site/jacoco/index.html`
- Frontend: Coverage report at `coverage/index.html`

---

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Browser                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ React UI     │  │  HLS.js      │  │  Local       │     │
│  │ Components   │  │  Player      │  │  Storage     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
         │ REST API           │ HLS Stream         │ State Persistence
         │ (JSON)             │ (.m3u8/.ts)        │ (UUID)
         ▼                    ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Spring Boot  │  │  Embedded    │  │  CORS        │     │
│  │ REST API     │  │  Tomcat      │  │  Filter      │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
         │                                           │
         │ JDBC                                      │ File System
         ▼                                           ▼
┌──────────────────┐                      ┌──────────────────┐
│   PostgreSQL     │                      │   Static Assets  │
│   Database       │                      │   (Album Art)    │
│                  │                      │                  │
│ - songs          │                      │ /albumArtwork/   │
│ - ratings        │                      └──────────────────┘
└──────────────────┘
```

### Architecture Layers

#### 1. Presentation Layer (Frontend)
- **Technology**: React 19 + Vite
- **Port**: 5171
- **Responsibilities**:
  - User interface rendering
  - HLS stream playback
  - User interaction handling
  - Local state management
  - API communication

#### 2. Application Layer (Backend)
- **Technology**: Spring Boot 3.2.1 + Embedded Tomcat
- **Port**: 8081
- **Responsibilities**:
  - REST API endpoints
  - Business logic execution
  - Request/response handling
  - CORS management
  - Input validation

#### 3. Data Access Layer
- **Technology**: Spring Data JPA + Hibernate
- **Responsibilities**:
  - Entity management
  - Query execution
  - Transaction management
  - Connection pooling

#### 4. Persistence Layer
- **Technology**: PostgreSQL 16
- **Port**: 5432
- **Responsibilities**:
  - Data storage
  - Query optimization
  - Data integrity
  - ACID compliance

---

## Component Design

### Backend Component Architecture

```
com.radioawa/
├── RadioawaApplication.java          → Spring Boot entry point
├── config/
│   └── WebConfig.java                → CORS and web configuration
├── controller/
│   ├── HealthController.java         → Health check endpoint
│   └── RatingController.java         → Rating API endpoints
├── service/
│   └── RatingService.java            → Business logic layer
├── repository/
│   ├── SongRepository.java           → Song data access
│   └── RatingRepository.java         → Rating data access
├── entity/
│   ├── Song.java                     → Song entity model
│   ├── Rating.java                   → Rating entity model
│   └── RatingType.java               → Enum for rating types
└── dto/
    ├── RatingRequest.java            → Rating submission DTO
    ├── RatingResponse.java           → Rating response DTO
    └── RatingCountsResponse.java     → Rating counts DTO
```

### Frontend Component Architecture

```
frontend/src/
├── main.jsx                          → React entry point
├── App.jsx                           → Root application component
├── components/
│   ├── RadioPlayer.jsx               → HLS audio player component
│   ├── NowPlaying.jsx                → Current song display component
│   └── SongRating.jsx                → Rating widget component
├── services/
│   └── ratingService.js              → API client for ratings
└── utils/
    └── userIdentity.js               → User ID management
```

### Component Interaction Flow

```
User Action (Click Thumbs Up)
         │
         ▼
┌────────────────────┐
│  SongRating.jsx    │  ← Captures user interaction
└────────────────────┘
         │
         ▼
┌────────────────────┐
│ ratingService.js   │  ← Makes HTTP POST request
└────────────────────┘
         │
         ▼
┌────────────────────┐
│ RatingController   │  ← Receives REST request
│  - Validates input │
│  - Extracts IP     │
└────────────────────┘
         │
         ▼
┌────────────────────┐
│  RatingService     │  ← Executes business logic
│  - Rate limiting   │
│  - Vote processing │
└────────────────────┘
         │
         ▼
┌────────────────────┐
│ RatingRepository   │  ← Data access operations
│ SongRepository     │
└────────────────────┘
         │
         ▼
┌────────────────────┐
│   PostgreSQL DB    │  ← Persists data
└────────────────────┘
```

---

## Design Patterns & Principles

### Design Patterns Implemented

#### 1. **Layered Architecture Pattern**
- **Location**: Entire backend structure
- **Purpose**: Separation of concerns
- **Layers**:
  - Presentation (Controllers)
  - Business Logic (Services)
  - Data Access (Repositories)
  - Persistence (Entities)

#### 2. **Repository Pattern**
- **Location**: `repository/` package
- **Implementation**: Spring Data JPA interfaces
- **Benefits**:
  - Abstraction over data access
  - Query method generation
  - Transaction management

```java
public interface SongRepository extends JpaRepository<Song, Long> {
    Optional<Song> findByArtistAndTitle(String artist, String title);
}
```

#### 3. **Data Transfer Object (DTO) Pattern**
- **Location**: `dto/` package
- **Purpose**: Decouple API contracts from domain models
- **Examples**:
  - `RatingRequest` - Incoming rating data
  - `RatingResponse` - Outgoing rating data
  - `RatingCountsResponse` - Vote count data

#### 4. **Service Layer Pattern**
- **Location**: `service/` package
- **Purpose**: Encapsulate business logic
- **Features**:
  - Transaction management (`@Transactional`)
  - Business rule enforcement
  - Domain model coordination

#### 5. **Dependency Injection Pattern**
- **Implementation**: Spring Framework IoC Container
- **Benefits**:
  - Loose coupling
  - Testability
  - Configuration flexibility

```java
@Service
public class RatingService {
    private final SongRepository songRepository;
    private final RatingRepository ratingRepository;

    // Constructor injection
    public RatingService(SongRepository songRepository,
                        RatingRepository ratingRepository) {
        this.songRepository = songRepository;
        this.ratingRepository = ratingRepository;
    }
}
```

#### 6. **Factory Pattern (Implicit)**
- **Location**: Entity creation in `RatingService`
- **Purpose**: Create song entities when needed
- **Implementation**: `orElseGet()` with lambda

```java
Song song = songRepository.findByArtistAndTitle(artist, title)
    .orElseGet(() -> {
        Song newSong = new Song();
        newSong.setArtist(artist);
        newSong.setTitle(title);
        return songRepository.save(newSong);
    });
```

#### 7. **Strategy Pattern (Frontend)**
- **Location**: HLS.js implementation
- **Purpose**: Different playback strategies based on browser support
- **Implementation**: Native HLS vs. HLS.js library

#### 8. **Observer Pattern (Frontend)**
- **Location**: React component lifecycle and hooks
- **Purpose**: Reactive state updates
- **Implementation**: `useState`, `useEffect` hooks

### SOLID Principles

| Principle | Implementation |
|-----------|---------------|
| **Single Responsibility** | Each class has one reason to change (Controllers handle HTTP, Services handle business logic, Repositories handle data access) |
| **Open/Closed** | Services can be extended without modification (new rating types can be added) |
| **Liskov Substitution** | Repository interfaces can be swapped with different implementations |
| **Interface Segregation** | Controllers depend only on service interfaces they need |
| **Dependency Inversion** | High-level modules (Services) depend on abstractions (Repository interfaces) |

### RESTful API Design Principles

1. **Resource-Based URLs**: `/api/ratings`
2. **HTTP Verbs**: GET, POST for operations
3. **Stateless**: Each request contains all necessary information
4. **JSON Representation**: Standard data format
5. **Proper Status Codes**: 200 (OK), 429 (Rate Limit), 500 (Error)

---

## Data Architecture

### Entity-Relationship Diagram

```
┌─────────────────────────┐
│        Song             │
├─────────────────────────┤
│ id (PK)                 │←───┐
│ artist                  │    │
│ title                   │    │
│ thumbs_up_count         │    │
│ thumbs_down_count       │    │
│ created_at              │    │
│ updated_at              │    │
└─────────────────────────┘    │
                               │ Many-to-One
                               │
┌─────────────────────────┐    │
│        Rating           │    │
├─────────────────────────┤    │
│ id (PK)                 │    │
│ song_id (FK)            │────┘
│ user_id                 │
│ ip_address              │
│ rating_type             │
│ created_at              │
└─────────────────────────┘

Indexes:
- song_id + user_id (unique for duplicate prevention)
- ip_address + created_at (for rate limiting queries)
- artist + title (for song lookup)
```

### Database Schema

#### Songs Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated ID |
| artist | VARCHAR(255) | NOT NULL | Artist name |
| title | VARCHAR(255) | NOT NULL | Song title |
| thumbs_up_count | INTEGER | DEFAULT 0 | Total thumbs up |
| thumbs_down_count | INTEGER | DEFAULT 0 | Total thumbs down |
| created_at | TIMESTAMP | DEFAULT NOW() | Record creation time |
| updated_at | TIMESTAMP | DEFAULT NOW() | Last update time |

**Indexes**:
- `idx_artist_title` on (artist, title) - Fast song lookup

#### Ratings Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated ID |
| song_id | BIGINT | FOREIGN KEY → songs.id | Reference to song |
| user_id | VARCHAR(255) | NOT NULL | UUID from localStorage |
| ip_address | VARCHAR(45) | | IPv4/IPv6 address |
| rating_type | VARCHAR(20) | NOT NULL | THUMBS_UP or THUMBS_DOWN |
| created_at | TIMESTAMP | DEFAULT NOW() | Vote timestamp |

**Indexes**:
- `idx_song_user` on (song_id, user_id) - Prevent duplicate votes
- `idx_ip_created` on (ip_address, created_at) - Rate limiting queries

### Data Flow Diagrams

#### Rating Submission Flow

```
1. User clicks thumbs up/down
         ↓
2. Frontend captures: artist, title, userId (localStorage)
         ↓
3. HTTP POST to /api/ratings
         ↓
4. Backend extracts IP address from headers
         ↓
5. Check IP rate limit (20 votes/hour)
         ├─ Exceeded → Return 429 error
         └─ OK → Continue
         ↓
6. Find or create Song entity
         ↓
7. Check existing Rating for (song_id, user_id)
         ├─ Exists & Same → Return success (idempotent)
         ├─ Exists & Different → Update rating, adjust counts
         └─ Not exists → Create new rating, increment count
         ↓
8. Save to database (transactional)
         ↓
9. Return updated counts to frontend
         ↓
10. Frontend updates UI optimistically
```

#### Metadata Fetch Flow

```
1. HLS stream provides metadata update
         ↓
2. Frontend extracts artist and title
         ↓
3. HTTP GET to /api/ratings/counts?artist=X&title=Y&userId=Z
         ↓
4. Backend queries database
         ├─ Song exists → Return counts + user's vote
         └─ Song not exists → Return zeros
         ↓
5. Frontend displays counts and highlights user's vote
         ↓
6. HTTP GET to /albumArtwork/{artist}-{title}.jpg
         ├─ Exists → Display image
         └─ Not exists → Display placeholder
```

---

## API Design

### REST API Endpoints

#### Health Check

**Endpoint**: `GET /api/health`

**Purpose**: Service availability check

**Response**:
```json
{
  "status": "UP",
  "timestamp": "2024-12-18T10:30:00Z"
}
```

#### Submit Rating

**Endpoint**: `POST /api/ratings`

**Purpose**: Submit or update song rating

**Request Body**:
```json
{
  "artist": "Artist Name",
  "title": "Song Title",
  "userId": "uuid-v4-string",
  "ratingType": "THUMBS_UP"  // or "THUMBS_DOWN"
}
```

**Response** (200 OK):
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

**Response** (429 Too Many Requests):
```json
{
  "songId": null,
  "artist": null,
  "title": null,
  "thumbsUpCount": 0,
  "thumbsDownCount": 0,
  "userRating": null,
  "message": "Rate limit exceeded. Maximum 20 votes per hour allowed."
}
```

#### Get Rating Counts

**Endpoint**: `GET /api/ratings/counts`

**Purpose**: Fetch vote counts for a song

**Query Parameters**:
- `artist` (required): Artist name
- `title` (required): Song title
- `userId` (optional): User's UUID for highlighting their vote

**Response**:
```json
{
  "songId": 123,
  "artist": "Artist Name",
  "title": "Song Title",
  "thumbsUpCount": 42,
  "thumbsDownCount": 5,
  "userRating": "THUMBS_UP"  // or null if user hasn't voted
}
```

### API Client (Frontend)

**Location**: `frontend/src/services/ratingService.js`

**Methods**:
- `submitRating(artist, title, userId, ratingType)` → Promise
- `getRatingCounts(artist, title, userId)` → Promise

**Error Handling**:
- Network errors → Throw with message
- HTTP errors → Parse backend message and throw
- Timeout → 10-second default timeout

---

## Security Architecture

### Current Security Measures

#### 1. **IP-Based Rate Limiting**
- **Limit**: 20 votes per IP address per hour
- **Implementation**: Time-windowed query on `ratings` table
- **Purpose**: Prevent vote manipulation and abuse
- **Bypass Protection**: Uses IP + UUID hybrid identification

#### 2. **CORS Configuration**
- **Allowed Origin**: `http://localhost:5171` (development)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Credentials**: Enabled
- **Purpose**: Prevent unauthorized cross-origin requests

#### 3. **Input Validation**
- **Framework**: Jakarta Validation API
- **Validation Rules**:
  - Required fields: artist, title, userId, ratingType
  - String length limits
  - Enum validation for ratingType

#### 4. **SQL Injection Prevention**
- **Method**: JPA parameterized queries
- **Implementation**: All queries use named parameters (`:artist`, `:title`)
- **Result**: No raw SQL string concatenation

#### 5. **IP Address Extraction**
- **Headers Checked** (in order):
  1. X-Forwarded-For
  2. Proxy-Client-IP
  3. WL-Proxy-Client-IP
  4. HTTP_X_FORWARDED_FOR
  5. HTTP_X_FORWARDED
  6. HTTP_FORWARDED_FOR
  7. HTTP_FORWARDED
  8. HTTP_CLIENT_IP
  9. REMOTE_ADDR (fallback)
- **Purpose**: Accurate IP identification behind proxies/CDNs

#### 6. **Anonymous User Identification**
- **Method**: UUID v4 in localStorage
- **Benefits**: No personal data collection, GDPR-friendly
- **Limitation**: Cleared when browser storage is cleared

### Security Vulnerabilities (Current)

| Vulnerability | Risk Level | Description |
|---------------|-----------|-------------|
| No HTTPS | HIGH | All traffic in plaintext |
| No authentication | MEDIUM | Anyone can vote |
| Database credentials in config | HIGH | Credentials not encrypted |
| No API rate limiting | MEDIUM | Endpoint abuse possible |
| No CSRF protection | MEDIUM | Cross-site request forgery possible |
| console.log statements | LOW | Debugging info in production |
| No input sanitization | MEDIUM | XSS potential in metadata |

---

## Performance & Scalability

### Current Performance Characteristics

| Metric | Value | Notes |
|--------|-------|-------|
| Average Response Time | < 100ms | Database queries optimized with indexes |
| Concurrent Users | ~100 | Single server, no load balancing |
| Database Connections | 10 (default pool) | HikariCP default configuration |
| Memory Usage | ~512MB | Spring Boot application |
| HLS Segment Size | 10s | Default HLS.js configuration |

### Bottlenecks Identified

1. **Single Database Instance**: No replication or failover
2. **No Caching Layer**: Every request hits database
3. **No CDN**: Static assets served from application server
4. **Synchronous Processing**: All requests block on database
5. **No Horizontal Scaling**: Single application instance

### Optimization Opportunities

#### Short-term (Quick Wins)
- Add Redis caching for rating counts
- Enable Spring Boot Actuator for monitoring
- Configure database connection pool size
- Add database query logging and optimization
- Implement response compression (gzip)

#### Medium-term
- Add database read replicas
- Implement CDN for static assets
- Add API response caching (ETags)
- Optimize album artwork storage (object storage)
- Add database indexes for analytics queries

#### Long-term
- Horizontal scaling with load balancer
- Database sharding by song_id
- Implement event-driven architecture
- Add message queue for async processing
- Microservices architecture

---

## Operational Architecture

### Deployment Architecture (Current)

```
Development Environment (Local)

┌─────────────────────────────────────┐
│         Developer Machine            │
│                                      │
│  ┌────────────┐    ┌─────────────┐ │
│  │  Vite      │    │ Spring Boot │ │
│  │  Dev Server│    │ Application │ │
│  │  :5171     │    │    :8081    │ │
│  └────────────┘    └─────────────┘ │
│         │                  │         │
│         └──────────┬───────┘         │
│                    │                 │
│          ┌─────────▼────────┐       │
│          │   PostgreSQL     │       │
│          │     :5432        │       │
│          └──────────────────┘       │
└─────────────────────────────────────┘
```

### Process Management

**Start Scripts**: `start-all.sh`
- Starts PostgreSQL (if not running)
- Starts Spring Boot backend (port 8081)
- Starts Vite frontend (port 5171)
- Saves process IDs to `.pid` files

**Stop Scripts**: `stop-all.sh`
- Reads PIDs from `.pid` files
- Gracefully stops all processes
- Cleans up PID files

**Status Check**: `check-status.sh`
- Checks PostgreSQL status
- Checks backend process
- Checks frontend process
- Reports HTTP endpoint availability

### Monitoring & Observability (Not Implemented)

Current gaps:
- No application metrics
- No distributed tracing
- No centralized logging
- No alerting system
- No health check dashboard

---

## Future Enhancements

### Phase 1: Production Readiness (Critical)

#### 1.1 Security Hardening
- [ ] **HTTPS/TLS Implementation**
  - Obtain SSL/TLS certificates (Let's Encrypt)
  - Configure Spring Boot for HTTPS
  - Redirect HTTP to HTTPS
  - Enable HSTS headers

- [ ] **Secrets Management**
  - Externalize database credentials
  - Use environment variables or secrets manager (AWS Secrets Manager, HashiCorp Vault)
  - Encrypt sensitive configuration

- [ ] **Authentication & Authorization**
  - Implement OAuth 2.0 / OpenID Connect
  - Add JWT-based authentication
  - Role-based access control (RBAC)
  - Admin dashboard for moderation

- [ ] **API Security**
  - Add API rate limiting (Bucket4j, Redis)
  - Implement CSRF protection
  - Add request signing
  - Enable API key authentication for admin endpoints

- [ ] **Input Sanitization**
  - XSS prevention for metadata fields
  - HTML entity encoding
  - Content Security Policy headers

#### 1.2 Observability & Monitoring

- [ ] **Application Monitoring**
  - Spring Boot Actuator endpoints
  - Prometheus metrics export
  - Grafana dashboards
  - Custom business metrics (votes/minute, active users)

- [ ] **Logging**
  - Centralized logging (ELK stack or Splunk)
  - Structured JSON logging
  - Log level management
  - Audit logging for votes

- [ ] **Distributed Tracing**
  - Implement OpenTelemetry or Zipkin
  - Trace ID propagation
  - Performance profiling

- [ ] **Alerting**
  - Set up PagerDuty or similar
  - Define SLIs/SLOs
  - Configure alert thresholds
  - On-call rotation

#### 1.3 Testing & Quality Assurance

- [ ] **Unit Testing**
  - JUnit 5 tests for services
  - React Testing Library for components
  - Achieve 80%+ code coverage

- [ ] **Integration Testing**
  - TestContainers for database tests
  - API integration tests
  - End-to-end tests (Playwright or Cypress)

- [ ] **Load Testing**
  - JMeter or Gatling load tests
  - Performance benchmarking
  - Capacity planning

- [ ] **CI/CD Pipeline**
  - GitHub Actions or GitLab CI
  - Automated testing
  - Automated deployments
  - Blue-green or canary deployments

### Phase 2: Scalability & Performance

#### 2.1 Caching Strategy

- [ ] **Application-Level Caching**
  - Spring Cache abstraction
  - Redis for distributed caching
  - Cache rating counts (TTL: 5 minutes)
  - Cache song metadata

- [ ] **HTTP Caching**
  - ETags for API responses
  - Cache-Control headers
  - Conditional requests (If-None-Match)

- [ ] **CDN Integration**
  - CloudFlare or AWS CloudFront
  - Serve static assets from CDN
  - Edge caching for album artwork
  - HLS segment caching

#### 2.2 Database Optimization

- [ ] **Read Replicas**
  - PostgreSQL streaming replication
  - Read queries to replicas
  - Write queries to primary
  - Connection pooling per replica

- [ ] **Database Sharding**
  - Shard by song_id range
  - Consistent hashing for distribution
  - Shard-aware query routing

- [ ] **Query Optimization**
  - Add covering indexes
  - Materialized views for analytics
  - Denormalization for hot paths
  - Query result caching

- [ ] **Connection Pooling**
  - Tune HikariCP settings
  - Set max pool size based on load
  - Monitor connection usage

#### 2.3 Horizontal Scaling

- [ ] **Load Balancer**
  - NGINX or AWS ALB
  - Round-robin or least-connections
  - Health check configuration
  - Session affinity (if needed)

- [ ] **Stateless Application**
  - Remove all in-memory state
  - Session storage in Redis
  - Shared cache layer

- [ ] **Auto-Scaling**
  - Kubernetes HPA (Horizontal Pod Autoscaler)
  - Scale based on CPU/memory
  - Scale based on request rate
  - Predictive scaling

#### 2.4 Async Processing

- [ ] **Message Queue**
  - RabbitMQ or Apache Kafka
  - Async vote processing
  - Event sourcing for analytics
  - Dead letter queue for failures

- [ ] **Background Jobs**
  - Spring Batch or Quartz Scheduler
  - Aggregate vote counts (hourly)
  - Clean up old ratings
  - Generate analytics reports

### Phase 3: Advanced Features

#### 3.1 Analytics & Insights

- [ ] **Listener Analytics**
  - Real-time listener count
  - Geographic distribution
  - Listening duration tracking
  - Peak hours analysis

- [ ] **Song Analytics**
  - Top rated songs (daily/weekly/monthly)
  - Most played songs
  - Skip rate tracking
  - Genre preference analysis

- [ ] **Admin Dashboard**
  - React Admin or custom dashboard
  - Real-time metrics visualization
  - User management
  - Content moderation

#### 3.2 User Features

- [ ] **User Accounts**
  - Optional account registration
  - Profile management
  - Listening history
  - Favorite songs list

- [ ] **Social Features**
  - Share currently playing song
  - Playlist creation
  - Comments/chat (moderated)
  - Follow other listeners

- [ ] **Personalization**
  - Recommended songs based on ratings
  - Custom playlists
  - Notification preferences

#### 3.3 Content Management

- [ ] **Metadata Management**
  - Admin API for metadata updates
  - Bulk import from MusicBrainz
  - Album artwork auto-fetch
  - Metadata validation

- [ ] **Playlist Management**
  - Schedule-based playlists
  - Genre tagging
  - Rotation rules
  - Manual override capabilities

#### 3.4 Advanced Streaming

- [ ] **Adaptive Bitrate**
  - Multiple quality levels
  - Client-side quality switching
  - Network-based adaptation

- [ ] **DVR Functionality**
  - Rewind/replay recent segments
  - Time-shifted playback
  - Bookmark moments

- [ ] **Multi-Station Support**
  - Multiple radio stations
  - Station switching
  - Station-specific branding

### Phase 4: Enterprise Architecture

#### 4.1 Microservices Decomposition

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Streaming   │  │   Rating     │  │  Metadata    │
│   Service    │  │   Service    │  │   Service    │
└──────────────┘  └──────────────┘  └──────────────┘
       │                 │                  │
       └─────────────────┴──────────────────┘
                         │
                ┌────────▼────────┐
                │   API Gateway   │
                │   (Kong/Apigee) │
                └─────────────────┘
```

Services:
- **Streaming Service**: HLS delivery, CDN integration
- **Rating Service**: Vote processing, aggregation
- **Metadata Service**: Song info, album art
- **User Service**: Authentication, profiles
- **Analytics Service**: Metrics, reporting
- **Notification Service**: Real-time updates

#### 4.2 Event-Driven Architecture

- [ ] **Event Bus**
  - Apache Kafka or AWS EventBridge
  - Event types: SongPlayed, VoteCast, UserJoined
  - Event sourcing for audit trail

- [ ] **CQRS Pattern**
  - Separate read/write models
  - Command handlers for votes
  - Query handlers for analytics

#### 4.3 Multi-Region Deployment

- [ ] **Geographic Distribution**
  - Deploy in multiple AWS/Azure regions
  - Route 53 or Traffic Manager for DNS
  - Latency-based routing
  - Data replication across regions

- [ ] **Disaster Recovery**
  - RTO/RPO targets
  - Automated failover
  - Regular DR drills
  - Backup strategy (daily + continuous)

#### 4.4 Compliance & Governance

- [ ] **GDPR Compliance**
  - Right to be forgotten
  - Data portability
  - Consent management
  - Privacy policy

- [ ] **Accessibility**
  - WCAG 2.1 Level AA compliance
  - Screen reader support
  - Keyboard navigation
  - High contrast mode

- [ ] **Internationalization**
  - Multi-language support
  - Locale-based formatting
  - Translation management

### Estimated Timeline & Effort

| Phase | Duration | Team Size | Priority |
|-------|----------|-----------|----------|
| Phase 1 (Production Readiness) | 3-4 months | 3-4 engineers | CRITICAL |
| Phase 2 (Scalability) | 4-6 months | 4-6 engineers | HIGH |
| Phase 3 (Advanced Features) | 6-9 months | 6-8 engineers | MEDIUM |
| Phase 4 (Enterprise) | 9-12 months | 8-10 engineers | LOW |

### Key Performance Indicators (KPIs)

#### Availability
- **Target**: 99.9% uptime (43 minutes downtime/month)
- **Measurement**: Health check pings every 30 seconds

#### Performance
- **Target**: P95 response time < 200ms
- **Measurement**: Application Performance Monitoring

#### Scalability
- **Target**: Support 10,000 concurrent listeners
- **Measurement**: Load testing results

#### Reliability
- **Target**: < 0.1% error rate
- **Measurement**: Error rate monitoring

---

## Appendix

### A. Glossary

| Term | Definition |
|------|------------|
| **HLS** | HTTP Live Streaming - Apple's streaming protocol |
| **JPA** | Jakarta Persistence API - ORM specification |
| **DTO** | Data Transfer Object - Object for API communication |
| **CORS** | Cross-Origin Resource Sharing - Browser security policy |
| **CDN** | Content Delivery Network - Distributed server network |
| **CQRS** | Command Query Responsibility Segregation - Architectural pattern |
| **SLI** | Service Level Indicator - Metric for service health |
| **SLO** | Service Level Objective - Target for SLI |

### B. References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [HLS.js Documentation](https://github.com/video-dev/hls.js/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [HTTP Live Streaming RFC](https://datatracker.ietf.org/doc/html/rfc8216)

### C. Contact & Support

- **Project Repository**: [GitHub Link]
- **Documentation**: This file and related markdown files
- **Issue Tracker**: [GitHub Issues]

---

**Document Version**: 1.0
**Last Updated**: December 2024
**Author**: Sujit K Singh
**Status**: Living Document
