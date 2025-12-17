# Technology Stack & Architecture Guide

## 📋 Table of Contents
1. [Core Technologies](#core-technologies)
2. [Backend Stack](#backend-stack)
3. [Frontend Stack](#frontend-stack)
4. [Database & Storage](#database--storage)
5. [Development Tools](#development-tools)
6. [Architecture & Patterns](#architecture--patterns)
7. [Infrastructure](#infrastructure)

---

## 🎯 Core Technologies

### Language & Runtime

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Backend programming language - provides type safety, performance, enterprise features |
| **TypeScript** | 5.9.3 | Frontend type-safe JavaScript - catches errors at compile time, better IDE support |
| **Node.js** | 25.2.1 | JavaScript runtime for running frontend build tools and development server |
| **npm** | 11.6.2 | Node package manager - manages frontend dependencies and scripts |

---

## 🔧 Backend Stack

### Framework & Core Libraries

#### **Spring Boot** (v3.4.0)
```
Purpose: Enterprise Java framework that simplifies building production-ready applications
Key Features: Auto-configuration, embedded servers, production-ready features
```

### Spring Boot Starters (Dependencies)

#### 1. **spring-boot-starter-web**
```
Purpose: Build REST APIs and web applications using Spring MVC
Includes:
  - Spring MVC (Model-View-Controller framework)
  - Embedded Tomcat server
  - Jackson (JSON serialization/deserialization)
  - RESTful web services support
```

#### 2. **spring-boot-starter-data-jpa**
```
Purpose: Database access using JPA (Java Persistence API) with Hibernate
Features:
  - ORM (Object-Relational Mapping) - map Java objects to database tables
  - Repository pattern for database operations
  - Automatic query generation from method names
  - Transaction management
```

#### 3. **spring-boot-starter-security**
```
Purpose: Authentication and authorization for securing APIs
Features:
  - Basic authentication
  - CORS (Cross-Origin Resource Sharing) configuration
  - Method-level security
  - Password encoding
  - Session management
```

#### 4. **spring-boot-starter-validation**
```
Purpose: Bean validation using annotations (@NotBlank, @NotNull, etc.)
Features:
  - Input validation on API requests
  - Automatic error responses for invalid data
  - Custom validation rules
```

#### 5. **spring-boot-starter-actuator**
```
Purpose: Production-ready monitoring and management endpoints
Provides:
  - /actuator/health - Application health status
  - /actuator/metrics - Performance metrics
  - /actuator/info - Application information
```

#### 6. **spring-boot-starter-test**
```
Purpose: Testing framework for unit and integration tests
Includes:
  - JUnit 5 - Testing framework
  - Mockito - Mocking framework
  - AssertJ - Fluent assertions
  - Spring Test - Spring-specific testing utilities
```

#### 7. **spring-security-test**
```
Purpose: Testing utilities for Spring Security
Features:
  - Mock authentication in tests
  - Security context setup for testing
```

### Database Driver

#### **PostgreSQL JDBC Driver**
```
Purpose: Enables Java applications to connect to PostgreSQL database
Type: Runtime dependency (loaded at runtime, not compile time)
```

### Build Tool

#### **Maven** (v3.9.11)
```
Purpose: Build automation and dependency management
Features:
  - Download and manage dependencies
  - Compile Java code
  - Run tests
  - Package application as JAR/WAR
  - Maven wrapper (mvnw) - ensures consistent Maven version
```

#### **spring-boot-maven-plugin**
```
Purpose: Maven plugin for Spring Boot applications
Features:
  - Package application as executable JAR
  - Run application with mvn spring-boot:run
  - Repackage dependencies into single JAR
```

---

## 🎨 Frontend Stack

### Core Framework

#### **React** (v19.2.0)
```
Purpose: JavaScript library for building user interfaces
Features:
  - Component-based architecture
  - Virtual DOM for efficient rendering
  - Declarative UI
  - Hooks for state management
```

#### **React DOM** (v19.2.0)
```
Purpose: React renderer for web browsers
Features:
  - Renders React components to actual DOM
  - Event handling
  - Hydration for server-rendered content
```

### Build Tool & Dev Server

#### **Vite** (v7.2.4)
```
Purpose: Next-generation frontend build tool and dev server
Features:
  - Lightning-fast Hot Module Replacement (HMR)
  - Native ES modules support
  - Optimized production builds
  - Built on esbuild (written in Go, extremely fast)
Replaces: Webpack, Create React App
```

#### **@vitejs/plugin-react** (v5.1.1)
```
Purpose: Vite plugin for React support
Features:
  - Fast Refresh (hot reload for React components)
  - JSX transformation
  - React-specific optimizations
```

### Type System

#### **TypeScript** (v5.9.3)
```
Purpose: Typed superset of JavaScript
Benefits:
  - Static type checking
  - Better IDE autocomplete and intellisense
  - Catch errors before runtime
  - Self-documenting code
  - Refactoring safety
```

#### **@types/react** & **@types/react-dom**
```
Purpose: TypeScript type definitions for React
Benefit: Enables type checking for React code
```

#### **@types/node**
```
Purpose: TypeScript type definitions for Node.js APIs
Benefit: Type safety when using Node.js features in build scripts
```

### Code Quality Tools

#### **ESLint** (v9.39.1)
```
Purpose: JavaScript/TypeScript linter - finds and fixes code problems
Features:
  - Enforces coding standards
  - Catches common mistakes
  - Configurable rules
  - Auto-fix capabilities
```

#### **@eslint/js**
```
Purpose: ESLint JavaScript configuration
Contains: Recommended ESLint rules for JavaScript
```

#### **eslint-plugin-react-hooks** (v7.0.1)
```
Purpose: ESLint rules for React Hooks
Enforces:
  - Rules of Hooks (only call at top level)
  - Exhaustive dependencies in useEffect
```

#### **eslint-plugin-react-refresh** (v0.4.24)
```
Purpose: ESLint rules for React Fast Refresh
Ensures: Components are compatible with hot reload
```

#### **typescript-eslint** (v8.46.4)
```
Purpose: ESLint integration for TypeScript
Features:
  - TypeScript-specific linting rules
  - Type-aware linting
```

#### **globals** (v16.5.0)
```
Purpose: Global variable definitions for ESLint
Contains: List of global variables (window, document, etc.)
```

---

## 🗄️ Database & Storage

### **PostgreSQL** (v16)
```
Purpose: Open-source relational database management system (RDBMS)
Features:
  - ACID compliance (Atomicity, Consistency, Isolation, Durability)
  - Advanced SQL features (JSON, arrays, full-text search)
  - Excellent performance and reliability
  - Strong data integrity
  - Extensible with plugins
  - Supports complex queries and joins

Why PostgreSQL over MySQL:
  - Better standards compliance
  - More advanced features (window functions, CTEs)
  - Better JSON support
  - More reliable for complex queries
```

### Database Tools

#### **HikariCP** (included in Spring Boot)
```
Purpose: High-performance JDBC connection pool
Features:
  - Fast connection pooling
  - Minimal overhead
  - Automatic connection recovery
  - Connection leak detection
```

#### **Hibernate** (included in Spring Data JPA)
```
Purpose: Object-Relational Mapping (ORM) framework
Features:
  - Maps Java objects to database tables
  - Automatic SQL generation
  - Lazy loading
  - Caching
  - Query optimization
```

---

## 🛠️ Development Tools

### Package Managers

#### **Maven Wrapper (mvnw)**
```
Purpose: Ensures all developers use same Maven version
Benefit: No need to install Maven globally
Usage: ./mvnw [maven-command]
```

#### **npm**
```
Purpose: Node Package Manager for JavaScript/TypeScript
Features:
  - Install dependencies (npm install)
  - Run scripts (npm run dev)
  - Manage package versions
  - Publish packages
```

### Runtime Management

#### **Homebrew** (macOS)
```
Purpose: Package manager for macOS
Used for: Installing PostgreSQL, other development tools
```

#### **brew services**
```
Purpose: Manage background services (like PostgreSQL)
Features:
  - Start/stop services
  - Auto-start on boot
  - Service status checking
```

---

## 🏗️ Architecture & Patterns

### 1. **Three-Tier Architecture**

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (React Frontend - Port 5173)           │
│  - UI Components                        │
│  - User interaction                     │
│  - HTTP requests to backend             │
└─────────────────────────────────────────┘
                    ↓ HTTP/REST API
┌─────────────────────────────────────────┐
│         Application Layer               │
│  (Spring Boot Backend - Port 8080)      │
│  - Business logic                       │
│  - REST Controllers                     │
│  - Request validation                   │
│  - Security/Authentication              │
└─────────────────────────────────────────┘
                    ↓ JDBC
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  (PostgreSQL Database - Port 5432)      │
│  - Data persistence                     │
│  - Tables, indexes                      │
│  - ACID transactions                    │
└─────────────────────────────────────────┘
```

### 2. **Backend Architecture (Layered/N-Tier)**

```
Controller Layer (AlertController)
    ↓
    Purpose: Handle HTTP requests, return responses
    Pattern: MVC Controller

Repository Layer (AlertRepository)
    ↓
    Purpose: Database operations (CRUD)
    Pattern: Repository Pattern

Entity Layer (Alert)
    ↓
    Purpose: Domain model, maps to database table
    Pattern: Domain Model, JPA Entity

Configuration Layer (SecurityConfig)
    ↓
    Purpose: Cross-cutting concerns (security, CORS)
    Pattern: Configuration Object
```

### 3. **Design Patterns Used**

#### **MVC (Model-View-Controller)**
```
Location: Backend (Spring MVC)
Purpose: Separates concerns in web applications
Components:
  - Model: Alert entity (data structure)
  - View: JSON responses (REST API, no traditional views)
  - Controller: AlertController (handles requests)
```

#### **Repository Pattern**
```
Location: AlertRepository interface
Purpose: Abstraction layer for data access
Benefits:
  - Decouples business logic from data access
  - Easy to test (can mock repository)
  - Consistent interface for database operations
  - Can switch database implementation easily
```

#### **Dependency Injection (DI)**
```
Location: Throughout Spring application
Example: @Autowired AlertRepository in AlertController
Purpose: Loosely coupled components
Benefits:
  - Easy to test (inject mocks)
  - Flexible configuration
  - Spring manages object lifecycle
```

#### **DTO (Data Transfer Object) - Implicit**
```
Location: Alert entity serves as DTO in REST responses
Purpose: Transfer data between layers
Benefits:
  - Control what data is exposed in API
  - Decouple API structure from database schema
```

#### **Singleton Pattern**
```
Location: Spring Beans (Controllers, Services, Repositories)
Purpose: Single instance of each component
Managed by: Spring IoC Container
```

#### **Factory Pattern**
```
Location: Spring Bean Factory
Purpose: Create and wire application components
Benefit: Declarative object creation
```

#### **Front Controller Pattern**
```
Location: Spring DispatcherServlet
Purpose: Single entry point for all HTTP requests
Benefit: Centralized request handling
```

#### **Component-Based Architecture**
```
Location: React frontend
Purpose: Reusable, composable UI components
Benefits:
  - Code reusability
  - Separation of concerns
  - Easy to test individual components
```

#### **Hooks Pattern** (React)
```
Location: useState, useEffect in App.tsx
Purpose: State management and side effects in functional components
Benefits:
  - Cleaner than class components
  - Easier to reuse stateful logic
  - Better composition
```

### 4. **API Architecture**

#### **RESTful API**
```
Pattern: REST (Representational State Transfer)
Principles:
  - Resource-based URLs (/api/alerts)
  - HTTP methods (GET, POST, PUT, DELETE)
  - Stateless communication
  - JSON representation
  - Standard HTTP status codes

Endpoints:
  GET    /api/alerts           - List all alerts
  GET    /api/alerts/{id}      - Get specific alert
  POST   /api/alerts           - Create new alert
  PUT    /api/alerts/{id}      - Update alert
  DELETE /api/alerts/{id}      - Delete alert
  GET    /api/alerts/severity/{severity} - Filter by severity
  GET    /api/alerts/status/{status}     - Filter by status
```

#### **CORS (Cross-Origin Resource Sharing)**
```
Location: SecurityConfig
Purpose: Allow frontend (port 5173) to call backend (port 8080)
Configuration:
  - Allowed origins: localhost:3000, localhost:5173
  - Allowed methods: GET, POST, PUT, DELETE, OPTIONS
  - Credentials: Enabled
```

### 5. **Data Persistence Patterns**

#### **ORM (Object-Relational Mapping)**
```
Tool: Hibernate via Spring Data JPA
Purpose: Map Java objects to database tables
Example:
  Alert entity → alerts table
  Alert.id → alerts.id column
```

#### **Active Record Pattern** (via JPA)
```
Location: Alert entity with JPA annotations
Features:
  - @Entity marks class as database table
  - @Id marks primary key
  - @Column configures column properties
  - Lifecycle callbacks (@PrePersist, @PreUpdate)
```

#### **Convention over Configuration**
```
Example: Spring Data JPA method names
  findBySeverity(String severity) → SELECT * FROM alerts WHERE severity = ?
  findByStatus(String status)     → SELECT * FROM alerts WHERE status = ?
Benefit: No need to write SQL queries
```

### 6. **Security Patterns**

#### **Basic Authentication**
```
Location: Spring Security configuration
Credentials: admin/admin123 (development only)
Usage: HTTP Authorization header
```

#### **CSRF Protection** (Disabled for API)
```
Reason: Not needed for stateless REST APIs
Note: Should be enabled for traditional web apps
```

#### **Input Validation**
```
Pattern: Bean Validation with annotations
Example: @NotBlank on Alert.title
Purpose: Prevent invalid data from entering system
```

---

## 🌐 Infrastructure

### Web Servers & Application Servers

This project uses different servers for different layers:

```
┌─────────────────────────────────────────────────┐
│             DEVELOPMENT ARCHITECTURE            │
├─────────────────────────────────────────────────┤
│                                                 │
│  Frontend Layer (Port 5173)                     │
│  ┌───────────────────────────┐                 │
│  │   Vite Dev Server         │                 │
│  │  - Serves React app       │                 │
│  │  - Hot Module Replacement │                 │
│  │  - TypeScript compilation │                 │
│  │  - Development only       │                 │
│  └───────────────────────────┘                 │
│              ↓ HTTP/REST API (fetch)           │
│                                                 │
│  Backend Layer (Port 8080)                     │
│  ┌───────────────────────────┐                 │
│  │   Apache Tomcat 10.x      │                 │
│  │  (Embedded in Spring Boot)│                 │
│  │  - Servlet Container      │                 │
│  │  - Application Server     │                 │
│  │  - HTTP Request Handler   │                 │
│  │  - Thread Pool Management │                 │
│  └───────────────────────────┘                 │
│              ↓ JDBC/SQL                        │
│                                                 │
│  Database Layer (Port 5432)                    │
│  ┌───────────────────────────┐                 │
│  │   PostgreSQL Server       │                 │
│  │  - Database engine        │                 │
│  │  - Data persistence       │                 │
│  │  - Query processing       │                 │
│  └───────────────────────────┘                 │
└─────────────────────────────────────────────────┘
```

---

### 1. **Apache Tomcat (Backend Application Server)**

#### Overview
```
Name: Apache Tomcat
Version: 10.x (bundled with Spring Boot 3.4.0)
Type: Servlet Container + Application Server
Port: 8080
Mode: Embedded (packaged inside JAR file)
```

#### What is Tomcat?
Tomcat is a **web server** AND **servlet container** that:
- Handles HTTP requests and responses
- Runs Java web applications (Servlets, JSP)
- Manages application lifecycle
- Provides thread pooling for concurrent requests
- Hosts your Spring Boot REST API

#### How Tomcat is Included
```xml
<!-- In pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- This automatically includes embedded Tomcat -->
</dependency>
```

**No separate installation needed!** Tomcat is bundled inside your Spring Boot application.

#### Traditional vs Embedded Tomcat

**Traditional Way (Pre-Spring Boot):**
```
1. Install Tomcat server separately (e.g., /opt/tomcat)
2. Build application as WAR file
3. Deploy WAR to Tomcat's webapps/ folder
4. Configure server.xml, context.xml
5. Start Tomcat service separately
```

**Modern Way (Spring Boot Embedded):**
```
1. Tomcat included in application JAR
2. Run: java -jar myapp.jar
3. Tomcat starts automatically on port 8080
4. Self-contained, portable application
5. Same package works everywhere (dev, test, prod)
```

#### Tomcat Responsibilities

```
┌─────────────────────────────────────┐
│     Apache Tomcat Server            │
├─────────────────────────────────────┤
│                                     │
│  1. HTTP Server                     │
│     - Listen on port 8080           │
│     - Accept HTTP requests          │
│     - Send HTTP responses           │
│                                     │
│  2. Servlet Container               │
│     - Run Java Servlets             │
│     - Spring DispatcherServlet      │
│     - Servlet lifecycle management  │
│                                     │
│  3. Application Server              │
│     - Host Spring Boot app          │
│     - Manage thread pools           │
│     - Handle concurrent requests    │
│     - Session management            │
│     - Connection pooling            │
│                                     │
│  4. Static Resource Handler         │
│     - Serve static files            │
│     - Cache management              │
│                                     │
└─────────────────────────────────────┘
```

#### How Tomcat Starts

```java
// When you run: ./mvnw spring-boot:run
public static void main(String[] args) {
    SpringApplication.run(AiSocCopilotApplication.class, args);
}

// Spring Boot auto-configuration does this internally:
1. Detect spring-boot-starter-web in classpath
2. Auto-configure embedded Tomcat
3. Create Tomcat connector on port 8080
4. Start Tomcat embedded server
5. Deploy your Spring application to Tomcat
6. Register Spring's DispatcherServlet
7. Map your @RestController endpoints
8. Ready to handle HTTP requests!
```

#### Verification

**Check if Tomcat is running:**
```bash
# Check status
./check-status.sh
# Shows: Backend (Spring Boot) - Port 8080: ✓ RUNNING

# Check process
lsof -i :8080
# Shows Java process with Tomcat

# Check logs
tail backend.log
# Shows: "Tomcat started on port 8080 (http)"
```

**Tomcat startup log:**
```
INFO o.s.b.w.embedded.tomcat.TomcatWebServer  :
  Tomcat initialized with port 8080 (http)
INFO o.apache.coyote.http11.Http11NioProtocol :
  Starting ProtocolHandler ["http-nio-8080"]
INFO o.s.b.w.embedded.tomcat.TomcatWebServer  :
  Tomcat started on port 8080 (http) with context path '/'
```

#### Benefits of Embedded Tomcat

| Benefit | Description |
|---------|-------------|
| **Self-Contained** | Everything in one JAR file |
| **Portable** | Runs anywhere Java is installed |
| **Version Control** | Tomcat version locked with app |
| **Easier Deployment** | Just copy JAR and run |
| **Cloud-Native** | Perfect for containers (Docker, Kubernetes) |
| **Microservices** | Each service has its own server |
| **No Conflicts** | Different apps can use different Tomcat versions |
| **Simplified Config** | Configure via application.properties |

#### Configuration

**Tomcat settings in application.properties:**
```properties
# Server port
server.port=8080

# Thread pool configuration
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10

# Connection timeout
server.tomcat.connection-timeout=20000

# Maximum connections
server.tomcat.max-connections=10000

# Context path (default is /)
server.servlet.context-path=/

# Compression
server.compression.enabled=true
```

#### Alternative Embedded Servers

Spring Boot supports swapping Tomcat with alternatives:

**Jetty:**
```xml
<!-- Exclude Tomcat, add Jetty -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

**Undertow:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

**Why Tomcat is most popular:**
- Most mature and stable
- Best documentation
- Largest community
- Industry standard
- Spring Boot default choice

---

### 2. **Vite Dev Server (Frontend Development Server)**

#### Overview
```
Name: Vite Development Server
Version: 7.2.4
Type: Development server with HMR
Port: 5173
Built on: esbuild (Go) + Rollup
Mode: Development only (not for production)
```

#### What is Vite Dev Server?

A **modern development server** that:
- Serves your React application during development
- Provides instant Hot Module Replacement (HMR)
- Transforms TypeScript to JavaScript on-the-fly
- Serves static assets (HTML, CSS, images)
- Proxies API calls to backend (if configured)

#### Key Features

```
┌─────────────────────────────────────┐
│     Vite Development Server         │
├─────────────────────────────────────┤
│                                     │
│  1. Lightning Fast Startup          │
│     - Uses esbuild (written in Go) │
│     - 10-100x faster than Webpack   │
│     - Cold start < 1 second         │
│                                     │
│  2. Hot Module Replacement (HMR)    │
│     - Update without full refresh   │
│     - Preserves application state   │
│     - Instant feedback              │
│                                     │
│  3. On-Demand Compilation           │
│     - Only compiles requested files │
│     - Native ES modules support     │
│     - No bundling in development    │
│                                     │
│  4. TypeScript Support              │
│     - Transforms .tsx to .js        │
│     - Type checking via tsc         │
│     - Fast compilation              │
│                                     │
└─────────────────────────────────────┘
```

#### How Vite Works

```
1. Browser requests: http://localhost:5173/
   ↓
2. Vite serves index.html
   ↓
3. Browser requests: /src/main.tsx
   ↓
4. Vite transforms TypeScript → JavaScript
   ↓
5. Browser requests: /src/App.tsx
   ↓
6. Vite transforms on-demand
   ↓
7. Browser executes React app
```

**No bundling in development** - each file served separately as ES modules!

#### Vite vs Traditional Build Tools

| Feature | Vite | Webpack/CRA |
|---------|------|-------------|
| **Cold Start** | < 1 second | 10-30 seconds |
| **HMR Speed** | Instant | 1-5 seconds |
| **Build Speed** | Fast (esbuild + Rollup) | Slow (Webpack) |
| **Dev Experience** | Excellent | Good |
| **Bundle Size** | Optimized | Good |

#### Configuration

**vite.config.ts:**
```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Can add proxy for API calls
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

#### Development vs Production

**Development (Vite Dev Server):**
- Runs on port 5173
- Hot Module Replacement
- Source maps for debugging
- Fast refresh for React
- No bundling (ES modules)

**Production (After build):**
```bash
npm run build
# Creates: dist/ folder with optimized files
# - Bundled JavaScript (minified)
# - Bundled CSS (minified)
# - Optimized assets
# - Tree-shaken code
```

**Production deployment:**
- Serve `dist/` folder from Nginx/Apache/CDN
- Static files only
- No dev server needed

---

### 3. **PostgreSQL Server (Database Server)**

#### Overview
```
Name: PostgreSQL
Version: 16
Type: Relational Database Management System (RDBMS)
Port: 5432
Mode: Background service (always running)
Management: Homebrew services on macOS
```

#### What is PostgreSQL Server?

A **database server** that:
- Stores and manages data
- Processes SQL queries
- Ensures ACID transactions
- Handles multiple concurrent connections
- Provides data integrity and security

#### Architecture

```
┌─────────────────────────────────────┐
│     PostgreSQL Server               │
├─────────────────────────────────────┤
│                                     │
│  1. Connection Handler              │
│     - Listen on port 5432           │
│     - Accept client connections     │
│     - Authenticate users            │
│                                     │
│  2. Query Processor                 │
│     - Parse SQL queries             │
│     - Optimize execution plan       │
│     - Execute queries               │
│                                     │
│  3. Storage Engine                  │
│     - Store data in tables          │
│     - Manage indexes                │
│     - Handle transactions           │
│     - MVCC (Multi-Version Concurrency) │
│                                     │
│  4. Background Processes            │
│     - Autovacuum (cleanup)          │
│     - WAL writer (durability)       │
│     - Statistics collector          │
│                                     │
└─────────────────────────────────────┘
```

#### Why PostgreSQL Runs Always

Unlike application servers, PostgreSQL typically runs **continuously**:

**Reasons:**
1. **Multiple applications** may connect to same database server
2. **Data must be available** whenever applications need it
3. **Background maintenance** (autovacuum, checkpoints)
4. **Minimal resource usage** when idle (~50-100MB RAM)
5. **Fast response** - no startup delay
6. **Mirrors production** - databases run 24/7 in production

**Management:**
```bash
# Check status
brew services list | grep postgres

# Start (if stopped)
brew services start postgresql@16

# Stop (rarely needed)
brew services stop postgresql@16

# Restart (troubleshooting)
brew services restart postgresql@16
```

---

### Communication Protocols

#### **HTTP/REST (Frontend ↔ Backend)**
```
Protocol: HTTP 1.1
Format: JSON
Port: 8080
Method: fetch() API in JavaScript

Example:
  Frontend: fetch('http://localhost:8080/api/alerts')
  Backend: Tomcat receives request
  Response: JSON array of alerts
```

#### **JDBC (Backend ↔ Database)**
```
Protocol: PostgreSQL wire protocol
Driver: PostgreSQL JDBC driver
Port: 5432
Connection: HikariCP connection pool

Flow:
  Spring Data JPA → Hibernate → JDBC Driver → PostgreSQL
```

---

### Server Comparison

| Aspect | Tomcat | Vite Dev Server | PostgreSQL |
|--------|--------|-----------------|------------|
| **Type** | Application Server | Development Server | Database Server |
| **Port** | 8080 | 5173 | 5432 |
| **Language** | Java | JavaScript/Node | C |
| **Purpose** | Run Java web apps | Serve React app in dev | Store data |
| **Production** | Yes (embedded in JAR) | No (build to static files) | Yes (managed service) |
| **Start/Stop** | With application | With npm run dev | Always running |
| **Protocols** | HTTP, Servlet API | HTTP, WebSocket | PostgreSQL wire protocol |

---

### Production Deployment

#### Development (Current)
```
Frontend: Vite Dev Server (localhost:5173)
Backend:  Tomcat Embedded (localhost:8080)
Database: PostgreSQL Local (localhost:5432)
```

#### Production (Typical)
```
Frontend: Nginx/Apache/CloudFront serving static files
          (Built with: npm run build → dist/ folder)

Backend:  Same Tomcat (embedded in JAR)
          - Run as systemd service: java -jar app.jar
          - OR in Docker container
          - OR on cloud platform (AWS, Azure, GCP)

Database: PostgreSQL Managed Service
          - AWS RDS
          - Azure Database for PostgreSQL
          - Google Cloud SQL
          - OR self-hosted with replicas
```

---

### Inside the Spring Boot JAR

When you build the application:
```bash
./mvnw clean package
# Creates: target/copilot-0.0.1-SNAPSHOT.jar
```

**JAR contents:**
```
copilot-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/
│   │   └── com/aisoc/copilot/        ← Your application code
│   ├── lib/
│   │   ├── tomcat-embed-core-*.jar   ← Tomcat server
│   │   ├── tomcat-embed-websocket-*.jar
│   │   ├── spring-boot-*.jar         ← Spring framework
│   │   ├── spring-web-*.jar
│   │   ├── hibernate-core-*.jar      ← Hibernate ORM
│   │   └── postgresql-*.jar          ← PostgreSQL driver
├── META-INF/
│   └── MANIFEST.MF                    ← Main-Class: JarLauncher
└── org/springframework/boot/loader/   ← Spring Boot Loader
```

**It's a "fat JAR" (uber JAR)** - everything bundled in one file!

**To run:**
```bash
java -jar target/copilot-0.0.1-SNAPSHOT.jar
# Tomcat starts automatically
# Application runs on port 8080
```

---

### Key Takeaways

1. **Backend uses Apache Tomcat 10.x** - Embedded in Spring Boot JAR
2. **Frontend uses Vite Dev Server** - Development only, builds to static files
3. **Database uses PostgreSQL 16** - Runs continuously as background service
4. **Tomcat is both web server AND application server** - Handles HTTP + runs Java apps
5. **Embedded servers are modern best practice** - Simplifies deployment and scaling
6. **Development mirrors production** - Same Tomcat in both environments

---

## 📦 Project Structure

### Backend Structure
```
backend/
├── src/main/java/com/aisoc/copilot/
│   ├── AiSocCopilotApplication.java    # Main entry point
│   ├── controller/                      # REST endpoints
│   │   └── AlertController.java
│   ├── entity/                          # Database models
│   │   └── Alert.java
│   ├── repository/                      # Data access
│   │   └── AlertRepository.java
│   └── config/                          # Configuration
│       └── SecurityConfig.java
├── src/main/resources/
│   └── application.properties           # App configuration
├── pom.xml                              # Maven dependencies
└── mvnw                                 # Maven wrapper
```

### Frontend Structure
```
frontend/
├── src/
│   ├── App.tsx                          # Main React component
│   ├── App.css                          # Component styles
│   ├── main.tsx                         # Entry point
│   ├── index.css                        # Global styles
│   └── vite-env.d.ts                    # Vite types
├── package.json                         # npm dependencies
├── tsconfig.json                        # TypeScript config
├── vite.config.ts                       # Vite config
└── eslint.config.js                     # ESLint config
```

---

## 🔄 Data Flow

### Creating an Alert

```
1. User clicks "Create" in UI
   ↓
2. React component makes HTTP POST request
   fetch('http://localhost:8080/api/alerts', {
     method: 'POST',
     body: JSON.stringify(alertData)
   })
   ↓
3. Request hits Spring Boot backend
   DispatcherServlet → AlertController.createAlert()
   ↓
4. Controller validates input (@Valid annotation)
   ↓
5. Controller calls Repository
   alertRepository.save(alert)
   ↓
6. Repository uses Hibernate to generate SQL
   INSERT INTO alerts (...) VALUES (...)
   ↓
7. JDBC sends SQL to PostgreSQL
   ↓
8. PostgreSQL executes INSERT, returns generated ID
   ↓
9. Hibernate hydrates Alert object with ID
   ↓
10. Controller returns Alert as JSON response
   ↓
11. React updates UI with new alert
```

---

## 🎯 Technology Choices Explained

### Why Spring Boot?
- Industry standard for Java web applications
- Huge ecosystem and community
- Production-ready out of the box
- Excellent documentation
- Enterprise-level reliability

### Why React?
- Most popular UI library
- Large ecosystem of libraries
- Strong community support
- Virtual DOM for performance
- Great developer experience

### Why Vite over Webpack?
- 10-100x faster in development
- Instant Hot Module Replacement
- Simpler configuration
- Better developer experience
- Native ES modules support

### Why TypeScript?
- Catches bugs before runtime
- Better IDE support
- Self-documenting code
- Refactoring safety
- Industry trend

### Why PostgreSQL?
- Free and open source
- Most advanced open-source database
- Better for complex queries than MySQL
- Excellent documentation
- Strong data integrity guarantees

### Why Maven?
- Standard for Java projects
- Vast repository of Java libraries (Maven Central)
- Declarative dependency management
- Build lifecycle management
- IDE integration

---

## 📊 Performance Considerations

### Backend Optimizations
- HikariCP connection pooling (fast database connections)
- Hibernate second-level cache (optional, not configured yet)
- JPA lazy loading (load related data only when needed)
- JSON serialization with Jackson (fast JSON processing)

### Frontend Optimizations
- Vite's code splitting (smaller bundles)
- React's Virtual DOM (efficient updates)
- Development build vs production build
- Tree shaking (removes unused code)

### Database Optimizations
- Primary key index on alerts.id
- PostgreSQL query planner optimization
- Connection pooling (multiple connections available)

---

## 🔐 Security Features

1. **Spring Security** - Authentication & authorization
2. **CORS Configuration** - Prevents unauthorized cross-origin requests
3. **CSRF Protection** - (Disabled for stateless API, enable for traditional apps)
4. **Input Validation** - Prevents invalid data
5. **SQL Injection Prevention** - JPA/Hibernate parameterized queries
6. **XSS Prevention** - React escapes user input automatically

---

## 📈 Monitoring & Observability

### Spring Boot Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information

Future additions:
- Logging (SLF4J + Logback)
- Distributed tracing (Zipkin, Jaeger)
- Application Performance Monitoring (APM)

---

## 🚀 Deployment Considerations

### Backend Deployment
```
Build: ./mvnw clean package
Output: target/copilot-0.0.1-SNAPSHOT.jar
Run: java -jar target/copilot-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
```
Build: npm run build
Output: dist/ folder (static files)
Serve: Any static file server (Nginx, Apache, CDN)
```

### Database
```
Production: Managed PostgreSQL (AWS RDS, Azure Database, etc.)
Backup: Automated backups
Scaling: Read replicas for scaling reads
```

---

## 📚 Learning Resources

### Spring Boot
- https://spring.io/projects/spring-boot
- https://spring.io/guides

### React
- https://react.dev/

### TypeScript
- https://www.typescriptlang.org/docs/

### PostgreSQL
- https://www.postgresql.org/docs/

### Vite
- https://vite.dev/guide/

---

## 🎓 Key Takeaways

1. **Modern Stack**: Latest versions of all technologies
2. **Type Safety**: TypeScript frontend, Java backend
3. **Best Practices**: Separation of concerns, clean architecture
4. **Developer Experience**: Fast dev server, hot reload, good tooling
5. **Production Ready**: Monitoring, validation, security built-in
6. **Scalable**: Can handle growth with proper deployment
7. **Maintainable**: Clear structure, standard patterns

This stack represents **industry-standard enterprise web development** in 2025!
