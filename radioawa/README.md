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

- **High-Quality Audio**: 24-bit / 48 kHz lossless HLS streaming
- **Modern UI**: Clean, responsive design with radioawa branding
- **Real-Time Controls**: Play/pause, volume control, live status indicators
- **Cross-Browser Support**: Chrome, Firefox, Safari, Edge
- **Adaptive Streaming**: Automatic quality adjustment
- **Error Recovery**: Built-in error handling and stream recovery
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
├── backend/           # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/radioawa/
│   │   │   │   ├── RadioawaApplication.java
│   │   │   │   └── controller/
│   │   │   │       └── HealthController.java
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/          # React + Vite application
│   ├── src/
│   │   ├── components/
│   │   │   ├── RadioPlayer.jsx      # HLS streaming player
│   │   │   └── RadioPlayer.css
│   │   ├── App.jsx                   # Main app component
│   │   ├── App.css
│   │   ├── index.css                 # Global styles
│   │   └── main.jsx
│   ├── public/
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── *.sh scripts # Automation scripts
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

## Stream Configuration

The HLS stream URL is configured in the RadioPlayer component:

**File:** `frontend/src/components/RadioPlayer.jsx`

```javascript
const streamUrl = 'https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8'
```

**To change the stream URL:**
1. Open `frontend/src/components/RadioPlayer.jsx`
2. Locate the `streamUrl` constant (around line 14)
3. Update the URL to your HLS stream
4. Save the file (HMR will auto-reload)

**Supported formats:**
- HLS (.m3u8) - Recommended for adaptive streaming
- Audio codec: AAC, MP3
- Video codec: H.264 (for video streams)

## API Endpoints

### Postman Collection
Import the complete API collection for easy testing:
- **File:** `backend/radioawa-api-collection.postman.json`
- **Guide:** `POSTMAN-GUIDE.md`

Import to Postman: **Import** → Select file → Done!

### Health Check
- **GET** `/api/health` - Returns backend health status

Example response:
```json
{
  "status": "UP",
  "service": "Radioawa Backend",
  "timestamp": 1702834567890
}
```

### Song Ratings
- **POST** `/api/ratings` - Submit thumbs up/down rating
- **GET** `/api/ratings/song?artist={artist}&title={title}&userId={userId}` - Get rating counts

Example rating request:
```json
{
  "artist": "The Beatles",
  "title": "Hey Jude",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "ratingType": "THUMBS_UP"
}
```

See `backend/radioawa-api-collection.postman.json` for all endpoints and examples.

## Database Management

### Connect to PostgreSQL

```bash
# Using docker exec
psql -U radioawa -d radioawa

# Using psql directly (if installed)
psql -h localhost -p 5432 -U radioawa -d radioawa
```

Password: `radioawa_dev_password`

### Stop the Database

```bash
brew services down
```

### Reset the Database

```bash
brew services down -v  # Removes volumes
brew services up -d
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

- [SETUP.md](./SETUP.md) - Detailed setup and installation guide
- [DEPLOYMENT.md](./DEPLOYMENT.md) - Production deployment instructions

## Next Steps

- Add authentication and authorization
- Create database entities and repositories
- Build REST API endpoints for playlist management
- Add track metadata display
- Implement error handling
- Add testing (JUnit for backend, Vitest for frontend)
- Set up CI/CD pipeline
- Add logging and monitoring

## License

All rights reserved. radioawa © 2024
