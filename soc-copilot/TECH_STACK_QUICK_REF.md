# Technology Stack - Quick Reference

## 🎯 One-Liner Summary

**Modern full-stack web application**: React + TypeScript frontend communicating with Spring Boot + PostgreSQL backend via REST API.

---

## 📊 Quick Lookup Table

### Core Stack

| Component | Technology | Version | One-Liner Purpose |
|-----------|-----------|---------|-------------------|
| **Backend Language** | Java | 21 | Type-safe, enterprise-grade programming language |
| **Backend Framework** | Spring Boot | 3.4.0 | Production-ready Java web framework with auto-configuration |
| **Frontend Library** | React | 19.2.0 | Component-based UI library for building interactive interfaces |
| **Frontend Language** | TypeScript | 5.9.3 | Type-safe JavaScript with compile-time error checking |
| **Database** | PostgreSQL | 16 | Powerful open-source relational database with advanced SQL features |
| **Build Tool (Backend)** | Maven | 3.9.11 | Java dependency management and build automation |
| **Build Tool (Frontend)** | Vite | 7.2.4 | Lightning-fast frontend build tool with instant HMR |
| **Package Manager** | npm | 11.6.2 | Node package manager for JavaScript dependencies |
| **Node Runtime** | Node.js | 25.2.1 | JavaScript runtime for frontend development tools |

---

## 🔧 Backend Dependencies (Spring Boot)

| Dependency | Purpose |
|-----------|---------|
| **spring-boot-starter-web** | Build REST APIs with embedded Tomcat server |
| **spring-boot-starter-data-jpa** | Database access with Hibernate ORM and repository pattern |
| **spring-boot-starter-security** | Authentication, authorization, and CORS configuration |
| **spring-boot-starter-validation** | Input validation using annotations (@NotBlank, @Valid) |
| **spring-boot-starter-actuator** | Production monitoring endpoints (health, metrics, info) |
| **spring-boot-starter-test** | Testing with JUnit 5, Mockito, and Spring Test |
| **postgresql** | JDBC driver for PostgreSQL database connectivity |
| **spring-security-test** | Security testing utilities and mocks |
| **spring-boot-maven-plugin** | Package app as executable JAR and run with Maven |

---

## 🎨 Frontend Dependencies (React)

| Dependency | Type | Purpose |
|-----------|------|---------|
| **react** | Runtime | Core React library for building UIs |
| **react-dom** | Runtime | React renderer for web browsers |
| **@vitejs/plugin-react** | Dev | Vite plugin for React with Fast Refresh |
| **typescript** | Dev | TypeScript compiler for type checking |
| **@types/react** | Dev | TypeScript definitions for React |
| **@types/react-dom** | Dev | TypeScript definitions for React DOM |
| **@types/node** | Dev | TypeScript definitions for Node.js APIs |
| **eslint** | Dev | JavaScript/TypeScript code linter |
| **@eslint/js** | Dev | ESLint JavaScript configuration |
| **eslint-plugin-react-hooks** | Dev | ESLint rules for React Hooks |
| **eslint-plugin-react-refresh** | Dev | ESLint rules for Fast Refresh compatibility |
| **typescript-eslint** | Dev | TypeScript integration for ESLint |
| **globals** | Dev | Global variable definitions for ESLint |

---

## 🏗️ Architecture Patterns

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Three-Tier Architecture** | Overall | Frontend → Backend → Database separation |
| **MVC (Model-View-Controller)** | Backend | Separates data (Model), presentation (View/JSON), and logic (Controller) |
| **Repository Pattern** | Backend | Abstraction layer for database operations |
| **Dependency Injection** | Backend | Spring manages object creation and wiring |
| **RESTful API** | Backend | Stateless HTTP API with resource-based URLs |
| **Component-Based** | Frontend | Reusable, composable UI components |
| **Hooks Pattern** | Frontend | State and side effects in functional components |
| **ORM (Object-Relational Mapping)** | Backend | Maps Java objects to database tables |
| **Singleton** | Backend | Single instance of Spring beans |
| **Factory Pattern** | Backend | Spring IoC container creates objects |

---

## 🌐 Ports & Services

| Service | Port | Server Type | Purpose |
|---------|------|-------------|---------|
| **Frontend** | 5173 | Vite Dev Server | React development server with hot reload |
| **Backend** | 8080 | Apache Tomcat 10.x (Embedded) | Spring Boot REST API server + servlet container |
| **Database** | 5432 | PostgreSQL 16 | PostgreSQL database server (RDBMS) |

---

## 🖥️ Web Servers & Application Servers

### Backend: Apache Tomcat 10.x (Embedded)

| Aspect | Details |
|--------|---------|
| **Type** | Web Server + Servlet Container + Application Server |
| **Mode** | Embedded (inside Spring Boot JAR) |
| **Port** | 8080 |
| **Included in** | spring-boot-starter-web dependency |
| **Purpose** | Runs Java web applications and handles HTTP requests |
| **Production Ready** | Yes - same Tomcat in dev and production |

**Key Points:**
- ✅ No separate installation needed
- ✅ Bundled inside your JAR file
- ✅ Starts automatically with Spring Boot
- ✅ Handles HTTP, Servlets, JSP
- ✅ Thread pool for concurrent requests
- ✅ Cloud-native and microservices friendly

**Alternatives:** Jetty, Undertow (can swap, but Tomcat is most popular)

### Frontend: Vite Dev Server

| Aspect | Details |
|--------|---------|
| **Type** | Development server only |
| **Port** | 5173 |
| **Built on** | esbuild (Go) + Rollup |
| **Purpose** | Serve React app during development |
| **Production** | No - builds to static files (dist/) |

**Key Points:**
- ⚡ 10-100x faster than Webpack
- 🔥 Instant Hot Module Replacement (HMR)
- 📦 No bundling in dev (ES modules)
- 🚀 < 1 second cold start
- 🎯 TypeScript transformation on-the-fly

**Production:** Static files served by Nginx/Apache/CDN

### Database: PostgreSQL Server

| Aspect | Details |
|--------|---------|
| **Type** | Relational Database Management System (RDBMS) |
| **Port** | 5432 |
| **Mode** | Background service (always running) |
| **Purpose** | Store and manage data |
| **Management** | Homebrew services on macOS |

**Key Points:**
- 🗄️ ACID compliant
- ⚙️ Advanced SQL features
- 🔄 Runs continuously (24/7)
- 📊 Multiple concurrent connections
- 🔒 Strong data integrity

---

## 📦 Key Technologies Explained

### Backend Technologies

| Tech | What It Does | Why We Use It |
|------|-------------|---------------|
| **Spring Boot** | Framework for building Java web apps | Industry standard, production-ready, huge ecosystem |
| **Spring MVC** | Web framework within Spring | Handles HTTP requests/responses, routing |
| **Spring Data JPA** | Database abstraction layer | Simplifies database operations, auto-generates queries |
| **Hibernate** | ORM implementation | Maps Java objects to database tables automatically |
| **Spring Security** | Security framework | Authentication, authorization, CORS, CSRF protection |
| **Jackson** | JSON library | Converts Java objects ↔ JSON automatically |
| **HikariCP** | Connection pool | Fast, efficient database connection management |
| **Tomcat** | Web server | Embedded servlet container for running Java web apps |
| **Bean Validation** | Validation framework | Validates input using annotations |

### Frontend Technologies

| Tech | What It Does | Why We Use It |
|------|-------------|---------------|
| **React** | UI library | Component-based, virtual DOM, huge ecosystem |
| **TypeScript** | Typed JavaScript | Catches errors at compile time, better IDE support |
| **Vite** | Build tool & dev server | 10-100x faster than Webpack, instant HMR |
| **ESLint** | Code linter | Catches bugs, enforces code style |
| **JSX/TSX** | Syntax extension | Write HTML-like code in JavaScript/TypeScript |

### Database Technologies

| Tech | What It Does | Why We Use It |
|------|-------------|---------------|
| **PostgreSQL** | Relational database | Advanced SQL, reliable, open-source, ACID compliant |
| **JDBC** | Database API for Java | Standard way to connect Java apps to databases |
| **SQL** | Query language | Standard language for relational databases |

---

## 🔄 Data Flow Summary

```
User Browser
    ↓ (User interaction)
React Frontend (Port 5173)
    ↓ (HTTP/JSON)
Spring Boot Backend (Port 8080)
    ↓ (JDBC/SQL)
PostgreSQL Database (Port 5432)
```

---

## 🎯 Technology Decisions Summary

| Decision | Choice | Alternative | Why Chosen |
|----------|--------|-------------|------------|
| Backend Framework | Spring Boot | Node.js/Express | Enterprise features, type safety, performance |
| Frontend Library | React | Vue/Angular | Most popular, huge ecosystem, great docs |
| Build Tool | Vite | Webpack/CRA | 10-100x faster, better DX |
| Language (Backend) | Java | Kotlin/Scala | Industry standard, mature ecosystem |
| Language (Frontend) | TypeScript | JavaScript | Type safety, better tooling |
| Database | PostgreSQL | MySQL/MongoDB | Advanced features, reliability, ACID |
| ORM | Hibernate/JPA | MyBatis/JOOQ | Standard, less boilerplate |
| Package Manager | npm | yarn/pnpm | Most widely used, good performance |

---

## 📈 Layers & Responsibilities

### Backend Layers

```
┌─────────────────────────────────────┐
│  Controller Layer                   │  ← HTTP requests/responses
│  - AlertController                  │  ← REST endpoints
│  - @RestController, @RequestMapping │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Repository Layer                   │  ← Database operations
│  - AlertRepository                  │  ← CRUD methods
│  - extends JpaRepository            │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Entity Layer                       │  ← Data models
│  - Alert                            │  ← Maps to database table
│  - @Entity, @Table                  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Database                           │  ← Data storage
│  - alerts table                     │  ← Actual data
└─────────────────────────────────────┘
```

### Frontend Layers

```
┌─────────────────────────────────────┐
│  Components                         │  ← UI elements
│  - App.tsx (main component)        │  ← React components
│  - Uses hooks (useState, useEffect) │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  API Layer (fetch)                  │  ← HTTP client
│  - Makes REST API calls             │  ← Communication
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Backend REST API                   │  ← Server endpoints
└─────────────────────────────────────┘
```

---

## 🔐 Security Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Authentication | Spring Security | Basic auth (username/password) |
| CORS | Spring CORS Config | Allow frontend to call backend |
| Input Validation | Bean Validation | Prevent invalid data |
| SQL Injection Prevention | JPA/Hibernate | Parameterized queries |
| XSS Prevention | React | Auto-escapes user input |

---

## 🛠️ Development Tools

| Tool | Purpose |
|------|---------|
| **Maven Wrapper (mvnw)** | Ensures consistent Maven version across team |
| **Homebrew** | Package manager for installing PostgreSQL on Mac |
| **brew services** | Manage PostgreSQL as background service |
| **curl** | Test REST APIs from command line |
| **Postman** | Interactive API testing with GUI |

---

## 📁 File Types & Their Purpose

| Extension | Type | Purpose |
|-----------|------|---------|
| **.java** | Java source | Backend business logic |
| **.tsx** | TypeScript+JSX | React components with TypeScript |
| **.ts** | TypeScript | TypeScript code without JSX |
| **.css** | Stylesheet | UI styling |
| **.json** | JSON data | Configuration, dependencies |
| **.xml** | XML data | Maven configuration (pom.xml) |
| **.properties** | Config | Spring Boot configuration |
| **.md** | Markdown | Documentation |
| **.sh** | Shell script | Automation scripts |

---

## 🚀 Commands Quick Reference

### Backend
```bash
./mvnw spring-boot:run    # Run backend
./mvnw clean package      # Build JAR
./mvnw test               # Run tests
```

### Frontend
```bash
npm run dev               # Start dev server
npm run build             # Build for production
npm run lint              # Run linter
```

### Database
```bash
./db-utils.sh connect     # Connect to database
./db-utils.sh tables      # List tables
./db-utils.sh info        # Database info
```

### Services
```bash
./start-dev.sh            # Start both servers
./stop-all.sh             # Stop both servers
./check-status.sh         # Check service status
```

---

## 💡 Quick Tips

1. **Spring Boot = Spring + Auto-configuration** - Less boilerplate
2. **JPA = Java Persistence API** - Standard for ORM in Java
3. **REST = Representational State Transfer** - Stateless HTTP API
4. **CORS = Cross-Origin Resource Sharing** - Security feature
5. **HMR = Hot Module Replacement** - Updates without full reload
6. **ORM = Object-Relational Mapping** - Objects ↔ Database tables
7. **DI = Dependency Injection** - IoC (Inversion of Control)
8. **MVC = Model-View-Controller** - Separation of concerns

---

## 📦 Inside the Spring Boot JAR

When you build: `./mvnw clean package`

**Created:** `target/copilot-0.0.1-SNAPSHOT.jar` (Fat JAR / Uber JAR)

**Contains:**
```
copilot-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/          ← Your code
│   └── lib/
│       ├── tomcat-*.jar  ← Tomcat server
│       ├── spring-*.jar  ← Spring framework
│       ├── hibernate-*.jar ← Hibernate ORM
│       └── postgresql-*.jar ← PostgreSQL driver
├── META-INF/
│   └── MANIFEST.MF       ← Entry point
└── org/springframework/boot/loader/  ← Boot loader
```

**To run:** `java -jar copilot-0.0.1-SNAPSHOT.jar`
- Tomcat starts automatically ✅
- Runs on port 8080 ✅
- Self-contained, portable ✅

---

## 🔄 Server Comparison

### Development vs Production

| Layer | Development | Production |
|-------|-------------|------------|
| **Frontend** | Vite Dev Server (5173) | Nginx/Apache/CDN (static files) |
| **Backend** | Tomcat Embedded (8080) | Same Tomcat in JAR |
| **Database** | PostgreSQL Local (5432) | PostgreSQL Managed Service |

### Traditional vs Modern (Embedded Tomcat)

| Aspect | Traditional | Modern (Embedded) |
|--------|------------|-------------------|
| **Installation** | Install Tomcat separately | Bundled in JAR |
| **Deployment** | Deploy WAR to server | Run JAR file |
| **Configuration** | server.xml, context.xml | application.properties |
| **Portability** | Server-dependent | Runs anywhere |
| **Updates** | Update server separately | Update with app |
| **Microservices** | Difficult | Perfect fit |

---

## 📊 Technology Maturity

| Tech | Maturity | Ecosystem | Learning Curve |
|------|----------|-----------|----------------|
| Spring Boot | Very Mature | Huge | Medium |
| React | Very Mature | Huge | Low-Medium |
| TypeScript | Mature | Large | Low-Medium |
| PostgreSQL | Very Mature | Large | Medium |
| Vite | Mature | Growing | Low |

---

## 🎓 What Each Technology Replaces

| Modern Tech | Replaces/Improves | Why Better |
|------------|------------------|------------|
| Spring Boot | Plain Spring | Auto-configuration, less XML |
| React | jQuery, plain JS | Component model, virtual DOM |
| Vite | Webpack, CRA | 10-100x faster builds |
| TypeScript | JavaScript | Type safety, better tooling |
| JPA/Hibernate | JDBC, SQL | Less boilerplate, ORM |
| Maven | Ant | Declarative dependencies |

---

For detailed explanations, see **TECH_STACK_GUIDE.md**
