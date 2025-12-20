# radioawa - Production Deployment Guide

This guide covers deploying radioawa to production environments.

> **💡 Quick Deployment with Docker (Recommended)**: For the fastest and most reliable deployment, see [DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md). The Docker deployment is production-ready with security hardening, multi-stage builds, and automated database initialization.
>
> This guide is for **traditional/manual deployment** if you prefer VPS hosting or platform-as-a-service solutions.

## Table of Contents

1. [Deployment Overview](#deployment-overview)
2. [Pre-Deployment Testing](#pre-deployment-testing)
3. [Pre-Deployment Checklist](#pre-deployment-checklist)
4. [Building for Production](#building-for-production)
5. [Deployment Options](#deployment-options)
6. [Multi-Station Configuration](#multi-station-configuration)
7. [Environment Configuration](#environment-configuration)
8. [Security Considerations](#security-considerations)
9. [Monitoring and Maintenance](#monitoring-and-maintenance)

## Deployment Overview

### Architecture

```
┌─────────────────┐
│   Users/CDN     │
└────────┬────────┘
         │
    ┌────▼─────┐
    │ Frontend │ (Static files - React)
    │  Server  │ (Station Selector + Player + Ratings)
    └────┬─────┘
         │
    ┌────▼─────┐
    │ Backend  │ (Spring Boot API)
    │  Server  │ (Stations + Ratings + Metadata Proxy)
    └────┬─────┘
         │
    ┌────▼─────┐
    │Database  │ (PostgreSQL - Multi-Station)
    └──────────┘ (Stations, Songs, Ratings)
```

### Components to Deploy

1. **Frontend**: Static React build (HTML, CSS, JS)
   - Multi-station selector
   - HLS audio player
   - Song rating system

2. **Backend**: Spring Boot JAR file
   - REST API endpoints
   - Metadata proxy for Hindi station
   - IP-based rate limiting

3. **Database**: PostgreSQL (Required)
   - Station configurations (English & Hindi)
   - Song metadata (station-scoped)
   - User ratings (station-scoped, IP-tracked)

4. **External Streams**: HLS stream endpoints
   - English: CloudFront CDN
   - Hindi: All India Radio (Vividh Bharati)

## Pre-Deployment Testing

### Running the Test Suite

Before deploying to production, ensure all tests pass locally:

#### Backend Tests

```bash
# Run all backend tests
cd backend
mvn clean test

# Expected output:
# Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

# Generate code coverage report
mvn jacoco:report

# View the coverage report
open target/site/jacoco/index.html
```

**Backend Test Files**:
- `RatingControllerTest` - API endpoint tests
- `HealthControllerTest` - Health check tests

#### Frontend Tests

```bash
# Install dependencies (if not already installed)
cd frontend
npm install

# Run all frontend tests
npm run test

# Generate code coverage report
npm run test:coverage

# View the coverage report
open coverage/index.html
```

**Frontend Test Files**:
- `SongRating.test.jsx` - Rating component tests
- `ratingService.test.js` - API service tests

### Test Requirements Before Production

- ✅ All unit tests passing (backend: 6/6, frontend: configured)
- ✅ Code coverage minimum: 80% overall
- ✅ No critical security warnings
- ✅ API endpoints validated with Postman collection
- ✅ Linting passes: `npm run lint` (frontend)

### Integration Testing

Before production deployment, perform manual integration testing:

1. **Test Rating Submission**:
   - Submit ratings for multiple songs
   - Verify counts update correctly
   - Test rate limiting (20 votes/hour per IP)

2. **Test Station Switching**:
   - Switch between English and Hindi stations
   - Verify station persists on page reload
   - Verify ratings are station-specific

3. **Test Stream Playback**:
   - Play HLS stream
   - Verify audio quality (24-bit lossless)
   - Test volume control
   - Test pause/resume

4. **Test Error Handling**:
   - Test with offline backend
   - Test with invalid stream URL
   - Test with rate-limited IP

**API Testing Tools**:
- Postman collection: `backend/radioawa-api-collection.postman.json`
- cURL commands for manual testing
- Browser dev tools for frontend debugging

### Performance Testing

For production readiness:

```bash
# Backend performance test example (using Apache Bench)
ab -n 100 -c 10 http://localhost:8081/api/health

# Expected: Response time < 100ms, success rate > 99%
```

For detailed testing documentation, see [TESTING-FRAMEWORK.md](./TESTING-FRAMEWORK.md)

## Pre-Deployment Checklist

### Code Preparation

- [ ] All tests passing
- [ ] Code reviewed and merged
- [ ] Environment variables configured
- [ ] Stream URL verified
- [ ] Database migrations ready
- [ ] CORS settings configured
- [ ] Error handling implemented
- [ ] Logging configured

### Infrastructure

- [ ] Domain name registered
- [ ] SSL certificate obtained
- [ ] Server provisioned
- [ ] Database instance created
- [ ] CDN configured (optional)
- [ ] Monitoring tools set up
- [ ] Backup strategy defined

### Security

- [ ] Secrets stored securely
- [ ] API endpoints secured
- [ ] HTTPS enforced
- [ ] CORS properly configured
- [ ] Rate limiting implemented
- [ ] Security headers configured

## Building for Production

### Backend Build

#### 1. Update Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=${PORT:8080}

# Production profile
spring.profiles.active=production

# Database Configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logging
logging.level.root=INFO
logging.level.com.radioawa=INFO
```

#### 2. Build JAR File

```bash
cd backend
mvn clean package -DskipTests
```

Output: `backend/target/radioawa-backend-0.0.1-SNAPSHOT.jar`

#### 3. Test JAR Locally

```bash
java -jar target/radioawa-backend-0.0.1-SNAPSHOT.jar
```

### Frontend Build

#### 1. Update Configuration

Edit `frontend/src/components/RadioPlayer.jsx`:

```javascript
// Use environment variable or production URL
const streamUrl = import.meta.env.VITE_STREAM_URL ||
  'https://your-production-stream.com/live.m3u8'
```

Create `frontend/.env.production`:

```env
VITE_API_URL=https://api.yourdomain.com
VITE_STREAM_URL=https://stream.yourdomain.com/live.m3u8
```

#### 2. Build Static Files

```bash
cd frontend
npm run build
```

Output: `frontend/dist/` directory containing:
- `index.html`
- `assets/` (JS, CSS files)

#### 3. Test Build Locally

```bash
npm run preview
```

## Deployment Options

### Option 1: Traditional VPS (e.g., DigitalOcean, Linode)

#### Backend Deployment

1. **Upload JAR file to server:**

```bash
scp backend/target/radioawa-backend-0.0.1-SNAPSHOT.jar user@your-server:/opt/radioawa/
```

2. **Create systemd service:**

```bash
sudo nano /etc/systemd/system/radioawa-backend.service
```

```ini
[Unit]
Description=radioawa Backend
After=network.target

[Service]
Type=simple
User=radioawa
WorkingDirectory=/opt/radioawa
ExecStart=/usr/bin/java -jar /opt/radioawa/radioawa-backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

Environment="DATABASE_URL=jdbc:postgresql://localhost:5432/radioawa"
Environment="DB_USERNAME=radioawa"
Environment="DB_PASSWORD=your_secure_password"
Environment="PORT=8080"

[Install]
WantedBy=multi-user.target
```

3. **Start service:**

```bash
sudo systemctl daemon-reload
sudo systemctl enable radioawa-backend
sudo systemctl start radioawa-backend
sudo systemctl status radioawa-backend
```

#### Frontend Deployment with Nginx

1. **Copy build files:**

```bash
scp -r frontend/dist/* user@your-server:/var/www/radioawa/
```

2. **Configure Nginx:**

```bash
sudo nano /etc/nginx/sites-available/radioawa
```

```nginx
server {
    listen 80;
    server_name radioawa.com www.radioawa.com;

    root /var/www/radioawa;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Cache static assets
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # API proxy
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # SPA fallback
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

3. **Enable site and restart Nginx:**

```bash
sudo ln -s /etc/nginx/sites-available/radioawa /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

4. **Set up SSL with Let's Encrypt:**

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d radioawa.com -d www.radioawa.com
```

### Option 2: Platform as a Service

#### Heroku

**Backend:**

1. Create `Procfile` in backend directory:
```
web: java -jar target/radioawa-backend-0.0.1-SNAPSHOT.jar
```

2. Deploy:
```bash
cd backend
heroku create radioawa-backend
heroku addons:create heroku-postgresql:mini
git push heroku main
```

**Frontend:**

Use static build pack or deploy to Netlify/Vercel (see below).

#### Netlify (Frontend)

1. **Connect repository or upload build:**

```bash
cd frontend
npm run build
netlify deploy --prod --dir=dist
```

2. **Configure redirects:**

Create `frontend/public/_redirects`:
```
/api/*  https://your-backend.herokuapp.com/api/:splat  200
/*      /index.html  200
```

3. **Set environment variables:**
- Go to Site Settings → Build & Deploy → Environment
- Add `VITE_STREAM_URL`

#### Vercel (Frontend)

1. **Deploy:**

```bash
cd frontend
vercel --prod
```

2. **Configure:**

Create `vercel.json`:
```json
{
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "https://your-backend.com/api/:path*"
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ]
}
```

### Option 3: Docker Deployment (Recommended)

RadioAwa includes complete Docker support with development and production configurations. For detailed Docker deployment instructions, see **[DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md)**.

#### Quick Docker Deployment

**Development:**
```bash
docker compose up -d
# Access at http://localhost:5171
```

**Production:**
```bash
# Configure environment
./docker-setup.sh

# Deploy
docker compose -f docker-compose.prod.yml up -d

# View logs
docker compose -f docker-compose.prod.yml logs -f
```

**Features of Docker deployment:**
- ✅ Multi-stage builds (70-90% smaller images)
- ✅ Automatic database initialization with multi-station setup
- ✅ Hot reload in development
- ✅ Security hardening in production
- ✅ Health checks for all services
- ✅ Automatic CORS configuration
- ✅ Complete environment isolation

For full Docker documentation including:
- Architecture details
- Development vs production configurations
- Security features
- Troubleshooting
- Docker best practices

See: **[DOCKER-DEPLOYMENT.md](./DOCKER-DEPLOYMENT.md)**

## Multi-Station Configuration

### Station Management in Production

RadioAwa supports multiple radio stations stored in the database. Each station has:
- Unique code (e.g., `ENGLISH`, `HINDI`)
- Name and stream URL
- Metadata URL (external or proxy endpoint)
- Active status and display order

### View Current Stations

```bash
psql -U radioawa -d radioawa -c "SELECT code, name, stream_url, is_active FROM stations ORDER BY display_order;"
```

### Add a New Station

```sql
INSERT INTO stations (code, name, stream_url, metadata_url, is_active, display_order, created_at, updated_at)
VALUES (
    'PUNJABI',
    'RadioAwa Punjabi',
    'https://stream.example.com/punjabi.m3u8',
    '/api/metadata/punjabi',
    true,
    3,
    NOW(),
    NOW()
);
```

### Update Station Stream URL

```sql
UPDATE stations
SET stream_url = 'https://new-cdn.example.com/live.m3u8',
    updated_at = NOW()
WHERE code = 'ENGLISH';
```

### Disable a Station

```sql
UPDATE stations
SET is_active = false,
    updated_at = NOW()
WHERE code = 'HINDI';
```

**Note**: Station changes require no code deployment - the frontend automatically fetches available stations from the API.

### Default Stations

| Code | Name | Stream Source | Metadata |
|------|------|---------------|----------|
| `ENGLISH` | RadioAwa English | CloudFront CDN | External metadata |
| `HINDI` | RadioAwa Hindi - Vividh Bharati | All India Radio | Proxy endpoint |

## Environment Configuration

### Backend Environment Variables

```bash
# Database (Required)
DATABASE_URL=jdbc:postgresql://localhost:5432/radioawa
DB_USERNAME=radioawa
DB_PASSWORD=secure_password_here

# Server
PORT=8080
SPRING_PROFILES_ACTIVE=production

# CORS (Configure for your domain)
CORS_ALLOWED_ORIGINS=https://radioawa.com,https://www.radioawa.com

# Rate Limiting (Optional - defaults to 20)
RATE_LIMIT_VOTES_PER_HOUR=20
```

### Frontend Environment Variables

```bash
# API Backend URL
VITE_API_URL=https://api.radioawa.com

# Station streams are managed in database - no frontend env vars needed
```

## Security Considerations

### 1. HTTPS/SSL

- Use Let's Encrypt for free SSL certificates
- Enforce HTTPS (redirect HTTP → HTTPS)
- Use HSTS headers

### 2. Security Headers

Add to Nginx configuration:

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
```

### 3. Database Security

- Use strong passwords
- Restrict database access to backend only
- Enable SSL for database connections
- Regular backups

### 4. API Security

- Implement rate limiting
- Use CORS appropriately
- Validate all inputs
- Implement authentication (if needed)

### 5. Secrets Management

Never commit secrets! Use:
- Environment variables
- Secret management services (AWS Secrets Manager, HashiCorp Vault)
- Encrypted configuration files

## Monitoring and Maintenance

### Logging

**Backend logging:**

```properties
# application.properties
logging.file.name=/var/log/radioawa/backend.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.level.com.radioawa=INFO
```

**View logs:**

```bash
# Systemd service
sudo journalctl -u radioawa-backend -f

# Docker
docker logs -f radioawa-backend
```

### Monitoring Tools

**Basic monitoring:**

```bash
# Check service status
sudo systemctl status radioawa-backend

# Check resource usage
htop

# Check disk space
df -h

# Check network
netstat -tulpn | grep LISTEN
```

**Advanced monitoring:**

- **Application**: New Relic, DataDog, Prometheus
- **Infrastructure**: Grafana, Netdata
- **Uptime**: UptimeRobot, Pingdom
- **Logs**: ELK Stack (Elasticsearch, Logstash, Kibana)

### Health Checks

Set up automated health checks:

```bash
# Simple health check script
#!/bin/bash
response=$(curl -s -o /dev/null -w "%{http_code}" https://api.radioawa.com/api/health)
if [ $response != "200" ]; then
    echo "Backend is down!" | mail -s "radioawa Alert" admin@radioawa.com
fi
```

Add to crontab:
```bash
*/5 * * * * /opt/radioawa/health-check.sh
```

### Backup Strategy

**Database backups:**

```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/backups/radioawa"
DATE=$(date +%Y%m%d_%H%M%S)
docker exec radioawa-postgres pg_dump -U radioawa radioawa > "$BACKUP_DIR/backup_$DATE.sql"

# Keep only last 30 days
find $BACKUP_DIR -name "backup_*.sql" -mtime +30 -delete
```

Add to crontab:
```bash
0 2 * * * /opt/radioawa/backup.sh
```

### Updates and Maintenance

**Update process:**

1. Test updates in staging environment
2. Create database backup
3. Put application in maintenance mode
4. Deploy new version
5. Run database migrations
6. Test deployment
7. Remove maintenance mode
8. Monitor for issues

**Zero-downtime deployment:**

- Use blue-green deployment
- Use rolling updates with Docker Swarm/Kubernetes
- Use load balancer for seamless switching

## Performance Optimization

### Frontend

- Enable Gzip/Brotli compression
- Use CDN for static assets
- Implement caching headers
- Optimize images and assets
- Use code splitting

### Backend

- Enable response compression
- Implement caching (Redis)
- Database connection pooling
- Optimize database queries
- Use async operations

### Database

- Create indexes on frequently queried columns
- Regular VACUUM and ANALYZE
- Monitor query performance
- Implement read replicas for scaling

## Troubleshooting Production Issues

### Application won't start

```bash
# Check logs
sudo journalctl -u radioawa-backend -n 100

# Check if port is available
sudo lsof -i :8080

# Check disk space
df -h

# Check memory
free -m
```

### High CPU/Memory usage

```bash
# Check processes
top
htop

# Check Java heap usage
jstat -gc <PID>

# Adjust JVM settings
java -Xmx2g -Xms512m -jar app.jar
```

### Database connection issues

```bash
# Check database is running
docker ps | grep postgres

# Check connection
psql -h localhost -U radioawa -d radioawa

# Check connection limits
SELECT * FROM pg_stat_activity;
```

## Rollback Procedure

If deployment fails:

1. **Stop new version:**
```bash
sudo systemctl stop radioawa-backend
```

2. **Restore previous version:**
```bash
sudo cp /opt/radioawa/backup/radioawa-backend.jar /opt/radioawa/
```

3. **Restore database (if needed):**
```bash
psql -U radioawa radioawa < /backups/radioawa/latest_backup.sql
```

4. **Restart service:**
```bash
sudo systemctl start radioawa-backend
```

## Cost Optimization

### Estimated Monthly Costs

**Small deployment:**
- VPS (2 vCPU, 4GB RAM): $12-20
- Database: $7-15
- Domain: $10-15/year
- Total: ~$20-35/month

**Medium deployment:**
- VPS (4 vCPU, 8GB RAM): $40-60
- Managed database: $15-25
- CDN: $5-10
- Total: ~$60-95/month

**Tips to reduce costs:**
- Use reserved instances for long-term savings
- Implement auto-scaling
- Use CDN for static assets
- Optimize database queries
- Monitor and eliminate waste

## Support and Resources

- [Main README](./README.md)
- [Setup Guide](./SETUP.md)
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- React Documentation: https://react.dev/
- HLS.js Documentation: https://github.com/video-dev/hls.js/

---

**Note**: This guide provides general deployment instructions. Specific requirements may vary based on your infrastructure and requirements. Always test deployments in a staging environment first!
