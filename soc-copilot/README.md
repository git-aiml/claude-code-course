# Security Operations Center(SOC) Copilot - Enterprise Security Operations Center

A full-stack enterprise-grade AI Security Operations Center (SOC) application built with Spring Boot and React.

## Technology Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.4.0** - Enterprise Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database ORM
- **PostgreSQL 16** - Production-grade relational database
- **Maven** - Build and dependency management

### Frontend
- **React 18** with **TypeScript** - Type-safe UI development
- **Vite** - Fast build tool and dev server
- **Modern ES6+** - Latest JavaScript features

## Architecture

```
├── backend/                # Spring Boot REST API
│   ├── src/main/java/
│   │   └── com/aisoc/copilot/
│   │       ├── config/     # Security & CORS configuration
│   │       ├── controller/ # REST API endpoints
│   │       ├── entity/     # JPA entities
│   │       └── repository/ # Data access layer
│   └── pom.xml
│
├── frontend/               # React TypeScript application
│   ├── src/
│   │   ├── App.tsx        # Main application component
│   │   └── App.css        # Styling
│   └── package.json
│
└── README.md
```

## Current Features

### Backend API Endpoints
- `GET /api/alerts` - Retrieve all security alerts
- `GET /api/alerts/{id}` - Get specific alert by ID
- `POST /api/alerts` - Create new security alert
- `PUT /api/alerts/{id}` - Update existing alert
- `DELETE /api/alerts/{id}` - Delete alert
- `GET /api/alerts/severity/{severity}` - Filter by severity
- `GET /api/alerts/status/{status}` - Filter by status
- `GET /actuator/health` - Health check endpoint

### Frontend Features
- Real-time alert dashboard
- Severity-based color coding (HIGH, MEDIUM, LOW)
- Auto-refresh functionality
- Responsive grid layout
- Professional SOC-themed UI
- Error handling and loading states

## Database Schema

### Alerts Table
```sql
CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    source_ip VARCHAR(255),
    destination_ip VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Setup Instructions

### Prerequisites
- Java 21 installed
- PostgreSQL 16 installed and running
- Node.js 18+ and npm installed

### Backend Setup

1. Start PostgreSQL (already running via Homebrew):
```bash
brew services start postgresql@16
```

2. Database is already created: `aisoc_copilot`

3. Start the backend (currently running on port 8080):
```bash
cd backend
./mvnw spring-boot:run
```

Backend will be available at: `http://localhost:8080`

### Frontend Setup

1. Start the frontend development server (currently running on port 5173):
```bash
cd frontend
npm run dev
```

Frontend will be available at: `http://localhost:5173`

## Testing the Application

### Using cURL

Create a test alert:
```bash
curl -X POST http://localhost:8080/api/alerts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Suspicious Login Attempt",
    "description": "Multiple failed login attempts detected",
    "severity": "HIGH",
    "sourceIp": "203.0.113.45",
    "destinationIp": "10.0.0.100"
  }'
```

Get all alerts:
```bash
curl http://localhost:8080/api/alerts
```

### Using the Web Interface

1. Open browser to `http://localhost:5173`
2. View the AI SOC Copilot dashboard
3. Click "Refresh" to fetch latest alerts
4. Alerts are displayed with severity badges and details

## Current Status

✅ **Backend**: Running on port 8080
✅ **Frontend**: Running on port 5173
✅ **Database**: PostgreSQL connected and schema initialized
✅ **CORS**: Configured for local development
✅ **Security**: Basic configuration (development mode)

## Security Configuration (Development)

Current security setup is for **local development only**:
- CSRF disabled
- All `/api/**` endpoints permit all
- CORS allows `http://localhost:3000` and `http://localhost:5173`
- Basic authentication configured but not enforced on API endpoints

**⚠️ Production Deployment Requirements:**
- Enable CSRF protection
- Implement proper authentication (JWT/OAuth2)
- Configure HTTPS/TLS
- Set up proper CORS origins
- Enable Spring Security for all endpoints
- Add rate limiting
- Implement audit logging
- Set up monitoring and alerting

## Next Steps for Production

1. **Authentication & Authorization**
   - Implement JWT-based authentication
   - Add role-based access control (RBAC)
   - Integrate with enterprise SSO/SAML

2. **AI Integration**
   - Connect to threat intelligence feeds
   - Add ML models for threat detection
   - Implement automated alert triage

3. **Advanced Features**
   - Real-time WebSocket updates
   - Alert correlation and aggregation
   - Custom dashboards and reporting
   - Email/Slack notifications

4. **Infrastructure**
   - Docker containerization
   - Kubernetes deployment
   - CI/CD pipeline
   - Load balancing and scaling

## Cost Estimation (AWS)

### Development/Staging
- EC2 t3.medium: ~$30-40/month
- RDS PostgreSQL db.t3.micro: ~$15/month
- **Total: ~$45-55/month**

### Production (Small Scale)
- ECS Fargate (2 tasks): ~$50-80/month
- RDS PostgreSQL db.t3.small: ~$25-35/month
- Application Load Balancer: ~$20/month
- **Total: ~$95-135/month**

### Production (Medium Scale)
- EKS cluster + workers: ~$150-250/month
- RDS PostgreSQL db.r5.large: ~$150-200/month
- Load balancers, monitoring: ~$50-100/month
- **Total: ~$350-550/month**

## Stopping the Services

```bash
# Stop Spring Boot backend
# Press Ctrl+C in the terminal running ./mvnw spring-boot:run

# Stop React frontend
# Press Ctrl+C in the terminal running npm run dev

# Stop PostgreSQL
brew services stop postgresql@16
```

## Database Management

Connect to PostgreSQL:
```bash
/opt/homebrew/opt/postgresql@16/bin/psql aisoc_copilot
```

View alerts:
```sql
SELECT * FROM alerts;
```

## Troubleshooting

### Backend won't start
- Check if PostgreSQL is running: `brew services list`
- Verify database exists: `/opt/homebrew/opt/postgresql@16/bin/psql -l | grep aisoc`
- Check port 8080 is not in use: `lsof -i :8080`

### Frontend won't connect to backend
- Verify backend is running: `curl http://localhost:8080/api/alerts`
- Check browser console for CORS errors
- Ensure CORS is configured in SecurityConfig.java

### Database connection issues
- Check PostgreSQL logs: `tail -f /opt/homebrew/var/postgresql@16/server.log`
- Verify credentials in `backend/src/main/resources/application.properties`

## License

This is a prototype/development project for enterprise SOC operations.
