# radioawa - Lossless Audio Streaming Platform

**Author**: Sujit K Singh

Full-stack web application featuring HLS (HTTP Live Streaming) with 24-bit lossless audio quality, React frontend, Spring Boot backend, and PostgreSQL database.

## Tech Stack

- **Backend**: Spring Boot 3.2.1 + Tomcat (embedded)
- **Frontend**: React 19 + Vite 7
- **Streaming**: HLS.js (HTTP Live Streaming client)
- **Database**: PostgreSQL 16
- **Build Tools**: Maven (backend), npm (frontend)

## Features

### Audio Streaming
- **High-Quality Audio**: 24-bit / 48 kHz lossless HLS streaming
- **Multi-Station Support**: Switch between multiple radio stations (English & Hindi)
- **Adaptive Streaming**: Automatic quality adjustment
- **Cross-Browser Support**: Chrome, Firefox, Safari, Edge
- **Error Recovery**: Built-in error handling and stream recovery

### User Engagement
- **Song Ratings**: Thumbs up/down voting system with real-time counts
- **Now Playing**: Current song display with album artwork
- **Recently Played**: History of last 5 songs played
- **IP-Based Rate Limiting**: Anti-abuse protection (20 votes/hour/IP per station)
- **Station Isolation**: Independent ratings for each station

### User Experience
- **Modern UI**: Clean, responsive design with radioawa branding
- **Real-Time Controls**: Play/pause, volume control, live status indicators
- **Station Selector**: Easy switching between radio stations
- **Persistent Selection**: Last station choice remembered
- **Backend Health Monitoring**: Real-time backend status display

## Prerequisites

Make sure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- Node.js 18+ and npm
- PostgreSQL 16 (via Homebrew)

## Project Structure

```
radioawa/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/radioawa/
│   │   ├── RadioawaApplication.java  # Main Spring Boot app
│   │   ├── controller/               # REST API controllers
│   │   │   ├── HealthController.java
│   │   │   ├── RatingController.java
│   │   │   ├── StationController.java
│   │   │   └── MetadataProxyController.java
│   │   ├── service/                  # Business logic layer
│   │   │   └── RatingService.java
│   │   ├── repository/               # Data access layer
│   │   │   ├── SongRepository.java
│   │   │   ├── RatingRepository.java
│   │   │   └── StationRepository.java
│   │   ├── entity/                   # JPA entities
│   │   │   ├── Song.java
│   │   │   ├── Rating.java
│   │   │   ├── Station.java
│   │   │   └── RatingType.java
│   │   ├── dto/                      # Data transfer objects
│   │   │   ├── RatingRequest.java
│   │   │   ├── RatingResponse.java
│   │   │   ├── RatingCountsResponse.java
│   │   │   └── StationResponse.java
│   │   └── config/
│   │       └── WebConfig.java        # CORS configuration
│   ├── src/main/resources/
│   │   ├── application.properties    # App configuration
│   │   └── application.properties.example
│   ├── pom.xml                       # Maven dependencies
│   └── radioawa-api-collection.postman.json
│
├── frontend/                         # React + Vite application
│   ├── src/
│   │   ├── components/               # React components
│   │   │   ├── RadioPlayer.jsx       # HLS streaming player
│   │   │   ├── NowPlaying.jsx        # Now playing widget
│   │   │   ├── SongRating.jsx        # Rating widget
│   │   │   ├── StationSelector.jsx   # Station switcher
│   │   │   └── *.css                 # Component styles
│   │   ├── contexts/                 # React Context API
│   │   │   └── StationContext.jsx    # Station state management
│   │   ├── services/                 # API client services
│   │   │   ├── ratingService.js
│   │   │   └── stationService.js
│   │   ├── utils/                    # Utility functions
│   │   │   ├── userIdentity.js
│   │   │   └── stationStorage.js
│   │   ├── App.jsx                   # Main app component
│   │   ├── App.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── Documentation/                    # Markdown documentation
│   ├── README.md                     # This file
│   ├── TECHNICAL-ARCHITECTURE.md     # Complete architecture guide
│   ├── TESTING-FRAMEWORK.md          # Unit & integration testing guide
│   ├── MULTI-STATION-FEATURE.md      # Multi-station implementation
│   ├── HINDI-STATION-SETUP.md        # Hindi station setup guide
│   ├── IP-TRACKING-IMPLEMENTATION.md # Rate limiting details
│   ├── POSTMAN-GUIDE.md              # API testing guide
│   ├── QUICKSTART.md                 # Quick start guide
│   ├── SETUP.md                      # Detailed setup
│   └── DEPLOYMENT.md                 # Production deployment
│
└── Scripts/                          # Automation scripts
    ├── start-all.sh                  # Start all services
    ├── stop-all.sh                   # Stop all services
    ├── check-status.sh               # Check service status
    └── db-cli.sh                     # Database CLI tool
```

## Quick Start

### Using Automated Scripts (Recommended)

We provide convenient scripts to start and stop all services:

**Start everything:**
```bash
./start-all.sh
```

**Check status:**
```bash
./check-status.sh
```

**Stop everything:**
```bash
./stop-all.sh
```

**Access the player:**
```
http://localhost:5171
```

See [QUICKSTART.md](./QUICKSTART.md) for detailed script usage and manual setup options.

---

### Manual Setup (Alternative)

If you prefer to start services manually:

#### 1. Start the Database

```bash
brew services up -d
```

This starts PostgreSQL on port 5432 with:
- Database: `radioawa`
- Username: `radioawa`
- Password: `radioawa_dev_password`

Check database status:
```bash
brew services ps
```

#### 2. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend will start on http://localhost:8081

Test the backend:
```bash
curl http://localhost:8081/api/health
```

#### 3. Start the Frontend

Open a new terminal:

```bash
cd frontend
npm run dev
```

The frontend will start on http://localhost:5171

#### 4. Access the radioawa Player

Open your browser and navigate to:
```
http://localhost:5171
```

You should see:
- **radioawa streaming player** with animated logo
- **Play/Pause button** for stream control
- **Volume slider** for audio control
- **Live status indicator** showing stream status
- **Backend status** showing Spring Boot connection

**To start streaming:**
1. Click the large play button in the center
2. Adjust volume using the slider
3. Status will change from "Ready to Play" to "LIVE" when streaming

The player streams high-quality lossless audio from:
```
https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8
```

## Development

### Backend Development

The backend uses Spring Boot DevTools for hot reloading. Changes to Java files will automatically restart the server.

**Key directories:**
- `backend/src/main/java/com/radioawa/controller/` - REST controllers
- `backend/src/main/java/com/radioawa/config/` - Configuration classes
- `backend/src/main/resources/application.properties` - Application configuration

**Common tasks:**
```bash
# Run tests
mvn test

# Build JAR file
mvn clean package

# Run the JAR
java -jar target/radioawa-backend-0.0.1-SNAPSHOT.jar
```

### Frontend Development

Vite provides hot module replacement (HMR) for instant updates during development.

**Key files:**
- `frontend/src/App.jsx` - Main application component
- `frontend/src/components/RadioPlayer.jsx` - HLS streaming player component
- `frontend/vite.config.js` - Vite configuration (includes API proxy)

**Common tasks:**
```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint
```

## Testing

### Running Tests

#### Backend Tests (Spring Boot + JUnit 5)

```bash
# Run all tests
cd backend
mvn clean test

# Run specific test class
mvn test -Dtest=RatingControllerTest

# Generate code coverage report
mvn jacoco:report
open target/site/jacoco/index.html
```

**Test Files:**
- `backend/src/test/java/com/radioawa/controller/HealthControllerTest.java` - Health endpoint tests
- `backend/src/test/java/com/radioawa/controller/RatingControllerTest.java` - Rating API endpoint tests

#### Frontend Tests (React + Vitest)

```bash
# Install dependencies (first time only)
cd frontend
npm install

# Run all tests
npm run test

# Watch mode (re-run on file changes)
npm run test:watch

# Interactive UI
npm run test:ui

# Generate code coverage report
npm run test:coverage
open coverage/index.html
```

**Test Files:**
- `frontend/src/components/SongRating.test.jsx` - Song rating component tests
- `frontend/src/services/ratingService.test.js` - Rating API service tests

### Test Coverage

**Targets:**
- Overall: 80%+
- Statements: 80%+
- Functions: 80%+
- Branches: 75%+
- Lines: 80%+

**Current Status:**
- Backend: 6 unit tests passing ✅
- Frontend: Ready for testing (install dependencies first)

For detailed testing documentation, see [TESTING-FRAMEWORK.md](./TESTING-FRAMEWORK.md)

## Multi-Station Configuration

radioawa supports multiple radio stations with independent streams and metadata.

### Current Stations

| Station | Code | Status | Stream Source |
|---------|------|--------|---------------|
| RadioAwa English | ENGLISH | Active | CloudFront HLS |
| RadioAwa Hindi | HINDI | Active | All India Radio (AIR) |

### Changing Station Configuration

Stations are stored in the PostgreSQL database. To update a station:

```sql
UPDATE stations SET
  stream_url = 'https://your-stream-url.com/live.m3u8',
  metadata_url = 'https://your-metadata-api.com/json',
  updated_at = CURRENT_TIMESTAMP
WHERE code = 'HINDI';
```

### Adding a New Station

```sql
INSERT INTO stations (code, name, stream_url, metadata_url, is_active, display_order)
VALUES (
  'TAMIL',
  'RadioAwa Tamil',
  'https://your-stream.com/tamil/live.m3u8',
  'https://your-metadata.com/tamil/metadata.json',
  true,
  3
);
```

**Supported formats:**
- HLS (.m3u8) - Recommended for adaptive streaming
- Audio codec: AAC, MP3
- Video codec: H.264 (for video streams)

**See also:**
- [MULTI-STATION-FEATURE.md](./MULTI-STATION-FEATURE.md) - Complete multi-station architecture
- [HINDI-STATION-SETUP.md](./HINDI-STATION-SETUP.md) - Hindi station setup guide

## API Endpoints

### Postman Collection
Import the complete API collection for easy testing:
- **File:** `backend/radioawa-api-collection.postman.json`
- **Guide:** [POSTMAN-GUIDE.md](./POSTMAN-GUIDE.md)

Import to Postman: **Import** → Select file → Done!

### Health Check
**GET** `/api/health`

Returns backend health status.

```json
{
  "status": "UP",
  "service": "Radioawa Backend",
  "timestamp": 1702834567890
}
```

### Stations API
**GET** `/api/stations`

Returns list of all active radio stations.

```json
[
  {
    "id": 1,
    "code": "ENGLISH",
    "name": "RadioAwa English",
    "streamUrl": "https://...",
    "metadataUrl": "https://...",
    "isActive": true,
    "displayOrder": 1
  }
]
```

### Song Ratings API

**POST** `/api/ratings`

Submit a thumbs up/down rating for a song.

Request:
```json
{
  "artist": "Arijit Singh",
  "title": "Tum Hi Ho",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "ratingType": "THUMBS_UP",
  "stationCode": "HINDI"
}
```

Response:
```json
{
  "songId": 123,
  "artist": "Arijit Singh",
  "title": "Tum Hi Ho",
  "thumbsUpCount": 42,
  "thumbsDownCount": 5,
  "userRating": "THUMBS_UP",
  "message": "Rating submitted successfully"
}
```

**GET** `/api/ratings/counts?artist={artist}&title={title}&stationCode={code}&userId={userId}`

Get rating counts for a specific song.

```json
{
  "songId": 123,
  "artist": "Arijit Singh",
  "title": "Tum Hi Ho",
  "thumbsUpCount": 42,
  "thumbsDownCount": 5,
  "userRating": "THUMBS_UP"
}
```

### Metadata Proxy API

**GET** `/api/metadata/hindi`

Get simulated metadata for Hindi station.

```json
{
  "artist": "Arijit Singh",
  "title": "Tum Hi Ho",
  "album": "Aashiqui 2",
  "timestamp": "2024-12-19T08:02:34",
  "prev_artist_1": "Shreya Ghoshal",
  "prev_title_1": "Deewani Mastani"
}
```

**Rate Limiting:**
- Maximum 20 votes per hour per IP address per station
- Returns HTTP 429 (Too Many Requests) when limit exceeded

See [POSTMAN-GUIDE.md](./POSTMAN-GUIDE.md) for all endpoints and testing examples.

## Database Management

### Database CLI Tool

We provide an interactive CLI for common database operations:

```bash
./db-cli.sh
```

**Features:**
- List all tables
- View songs and ratings
- Show top rated songs
- Count records
- Run custom queries
- Clear data (with confirmation)

### Connect to PostgreSQL Manually

```bash
# Using psql (via Homebrew)
/opt/homebrew/opt/postgresql@16/bin/psql -U radioawa -d radioawa

# Or if psql is in PATH
psql -h localhost -p 5432 -U radioawa -d radioawa
```

Password: `radioawa_dev_password`

### Database Schema

**Tables:**
- `stations` - Radio station configurations
- `songs` - Song metadata (station-scoped)
- `ratings` - User ratings with IP tracking

**Relationships:**
- Songs belong to one Station (many-to-one)
- Ratings belong to one Song (many-to-one)

### Stop the Database

```bash
brew services stop postgresql@16
```

### Reset the Database

```bash
# Drop and recreate (caution: data loss!)
/opt/homebrew/opt/postgresql@16/bin/psql -U radioawa -d postgres -c "DROP DATABASE radioawa;"
/opt/homebrew/opt/postgresql@16/bin/psql -U radioawa -d postgres -c "CREATE DATABASE radioawa;"
```

## Configuration

### Environment Variables

Copy `.env.example` to `.env` and adjust as needed:

```bash
cp .env.example .env
```

### Backend Configuration

Edit `backend/src/main/resources/application.properties` to change:
- Server port
- Database connection
- JPA/Hibernate settings
- CORS settings
- Logging levels

### Frontend Configuration

Edit `frontend/vite.config.js` to change:
- Development server port
- API proxy settings

## Troubleshooting

### Backend won't start
- Check if Java 17+ is installed: `java -version`
- Ensure PostgreSQL is running: `brew services ps`
- Check if port 8081 is available: `lsof -i :8081`

### Frontend won't start
- Check if Node.js is installed: `node -v`
- Ensure dependencies are installed: `npm install`
- Check if port 5171 is available: `lsof -i :5171`

### Database connection errors
- Verify PostgreSQL is running: `brew services ps`
- Check database logs: `brew services logs postgres`
- Verify credentials in `application.properties`

### CORS errors
- Ensure backend is running on port 8081
- Check CORS configuration in `WebConfig.java`
- Verify proxy settings in `vite.config.js`

### Radio stream not playing
- Verify the stream URL is accessible: Open the .m3u8 URL in your browser
- Check browser console for HLS errors (F12 → Console tab)
- Ensure your network allows streaming connections
- Try a different browser (Safari has native HLS support)
- Check if the stream is live and broadcasting

### Audio quality issues
- Check your internet connection speed
- Verify the stream quality settings
- Look for network congestion
- Check browser audio settings

### Player controls not working
- Ensure JavaScript is enabled in your browser
- Check browser console for errors
- Clear browser cache and reload
- Try disabling browser extensions

## Production Build

### Backend

```bash
cd backend
mvn clean package
java -jar target/radioawa-backend-0.0.1-SNAPSHOT.jar
```

### Frontend

```bash
cd frontend
npm run build
```

The production-ready files will be in `frontend/dist/`. Serve these with any static file server or integrate with the Spring Boot backend.

## Browser Support

The radioawa player works on all modern browsers:

| Browser | Version | HLS Support |
|---------|---------|-------------|
| Chrome  | 90+     | Via HLS.js  |
| Firefox | 88+     | Via HLS.js  |
| Safari  | 14+     | Native      |
| Edge    | 90+     | Via HLS.js  |

**Note:** Safari has native HLS support. Other browsers use the HLS.js library for playback.

## radioawa Branding

### Color Palette - Warm Orange Theme

| Name           | Hex     | RGB         | Usage                        |
|----------------|---------|-------------|------------------------------|
| Sunset Orange  | #FF6B35 | 255/107/53  | Primary brand, buttons       |
| Deep Orange    | #C1440E | 193/68/14   | Headers, dark accents        |
| Coral          | #FF8C61 | 255/140/97  | Highlights, status           |
| Burnt Sienna   | #8B4513 | 139/69/19   | Body text, contrast          |
| Peach          | #FFE8D6 | 255/232/214 | Light backgrounds, accents   |
| Cream          | #FFF5E6 | 255/245/230 | Backgrounds, cards           |
| White          | #FFFFFF | 255/255/255 | Text on dark, pure white     |

### Typography

- **Headings**: Montserrat (Google Fonts)
- **Body**: Open Sans (Google Fonts)
- **Fallback**: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif

## Documentation

### Getting Started
- [README.md](./README.md) - This file (overview and quick start)
- [QUICKSTART.md](./QUICKSTART.md) - Quick start guide with scripts
- [START-HERE.md](./START-HERE.md) - New user orientation
- [SETUP.md](./SETUP.md) - Detailed setup and installation

### Features & Implementation
- [TECHNICAL-ARCHITECTURE.md](./TECHNICAL-ARCHITECTURE.md) - Complete system architecture
- [MULTI-STATION-FEATURE.md](./MULTI-STATION-FEATURE.md) - Multi-station implementation guide
- [HINDI-STATION-SETUP.md](./HINDI-STATION-SETUP.md) - Hindi station configuration
- [IP-TRACKING-IMPLEMENTATION.md](./IP-TRACKING-IMPLEMENTATION.md) - Rate limiting and IP tracking
- [ARTWORK-SETUP.md](./ARTWORK-SETUP.md) - Album artwork configuration

### API & Testing
- [POSTMAN-GUIDE.md](./POSTMAN-GUIDE.md) - API testing with Postman
- `backend/radioawa-api-collection.postman.json` - Postman collection

### Deployment
- [DEPLOYMENT.md](./DEPLOYMENT.md) - Production deployment instructions

## Current Implementation Status

✅ **Completed Features:**
- Multi-station support (English & Hindi)
- HLS audio streaming with HLS.js
- Song rating system (thumbs up/down)
- IP-based rate limiting (20 votes/hour/IP per station)
- Now Playing widget with album artwork
- Recently Played history (5 songs)
- Station-scoped ratings isolation
- Metadata proxy for Hindi station
- PostgreSQL database with JPA/Hibernate
- RESTful API with Spring Boot
- Responsive React UI with Vite
- CORS configuration
- Error handling and recovery

## Future Enhancements

### Phase 1: Production Readiness
- [ ] HTTPS/TLS implementation
- [ ] Authentication and authorization (OAuth 2.0/JWT)
- [ ] API rate limiting middleware
- [ ] Comprehensive unit and integration tests
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Monitoring and observability (Prometheus/Grafana)

### Phase 2: Scalability
- [ ] Redis caching for rating counts
- [ ] Database read replicas
- [ ] CDN integration for static assets
- [ ] Load balancer for horizontal scaling
- [ ] Message queue for async processing

### Phase 3: Advanced Features
- [ ] User accounts and profiles
- [ ] Playlist management
- [ ] Social features (sharing, comments)
- [ ] Analytics dashboard
- [ ] Admin panel for station management
- [ ] Real-time listener count
- [ ] Multi-language support

See [TECHNICAL-ARCHITECTURE.md](./TECHNICAL-ARCHITECTURE.md) for detailed roadmap.

## License

All rights reserved. radioawa © 2024
