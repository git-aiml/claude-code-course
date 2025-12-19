# Multi-Station Feature Implementation

**Author**: Sujit K Singh
**Date**: December 2024
**Status**: Completed

## Overview

radioawa now supports multiple radio stations with independent streams, metadata, and ratings. Users can switch between different stations (e.g., English, Hindi) seamlessly from the UI.

## Features

### 1. Station Management
- **Multiple Stations**: Support for unlimited radio stations
- **Station Switching**: One-click station switching via dropdown selector
- **Persistent Selection**: Last selected station is remembered in browser
- **Station Isolation**: Each station has independent:
  - Stream URL (HLS playlist)
  - Metadata URL (now playing info)
  - Album artwork URL
  - Song ratings and counts

### 2. Station Configuration

Each station has the following properties:

| Property | Type | Description |
|----------|------|-------------|
| `id` | Long | Database primary key |
| `code` | String | Unique identifier (e.g., "ENGLISH", "HINDI") |
| `name` | String | Display name (e.g., "RadioAwa English") |
| `streamUrl` | String | HLS stream URL (.m3u8 playlist) |
| `metadataUrl` | String | JSON metadata endpoint |
| `albumArtUrl` | String | Album artwork URL (optional) |
| `isActive` | Boolean | Whether station is available for selection |
| `displayOrder` | Integer | Sort order in station selector |

### 3. Current Stations

#### RadioAwa English (ENGLISH)
- **Status**: Active
- **Stream**: CloudFront HLS stream
- **Type**: English language music
- **Display Order**: 1 (default)

#### RadioAwa Hindi (HINDI)
- **Status**: Active
- **Stream**: Test stream (Mux)
- **Type**: Hindi language music
- **Display Order**: 2

## Technical Implementation

### Backend Changes

#### 1. Database Schema

**New Table: `stations`**
```sql
CREATE TABLE stations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    stream_url VARCHAR(500) NOT NULL,
    metadata_url VARCHAR(500) NOT NULL,
    album_art_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Modified Table: `songs`**
- Added `station_id` foreign key column
- Songs are now scoped to a specific station
- Index on `(station_id, artist, title)` for fast lookup

**Modified Table: `ratings`**
- Indirectly linked to station through `song_id`
- Rate limiting now per station (20 votes/hour/IP per station)

#### 2. New Java Classes

**Entity Layer:**
- `Station.java` - Station entity with JPA annotations
- Modified `Song.java` - Added `@ManyToOne` relationship to Station
- Modified `Rating.java` - No changes (linked via Song)

**Repository Layer:**
- `StationRepository.java` - Spring Data JPA repository
  ```java
  Optional<Station> findByCode(String code);
  List<Station> findByIsActiveTrueOrderByDisplayOrder();
  ```
- Modified `SongRepository.java` - Added station-scoped queries
  ```java
  Optional<Song> findByStationAndArtistAndTitle(Station station, String artist, String title);
  ```
- Modified `RatingRepository.java` - Added station-aware rate limit queries
  ```java
  long countByStationAndIpAddressAndCreatedAtAfter(Station station, String ipAddress, LocalDateTime since);
  ```

**DTO Layer:**
- `StationResponse.java` - Station data transfer object for API responses

**Controller Layer:**
- `StationController.java` - REST endpoints for stations
  - `GET /api/stations` - List all active stations
  - `GET /api/stations/{code}` - Get station by code

**Service Layer:**
- Modified `RatingService.java` - Station-scoped rating logic
  - Rate limiting per station
  - Song lookup within station scope

#### 3. API Changes

**New Endpoints:**
```
GET /api/stations
Response: [
  {
    "id": 1,
    "code": "ENGLISH",
    "name": "RadioAwa English",
    "streamUrl": "https://...",
    "metadataUrl": "https://...",
    "albumArtUrl": "https://...",
    "isActive": true,
    "displayOrder": 1
  }
]
```

**Modified Endpoints:**

**POST /api/ratings**
```json
{
  "artist": "Artist Name",
  "title": "Song Title",
  "userId": "uuid-v4",
  "ratingType": "THUMBS_UP",
  "stationCode": "ENGLISH"  // NEW: Required field
}
```

**GET /api/ratings/song**
```
GET /api/ratings/song?artist=X&title=Y&userId=Z&stationCode=ENGLISH
                                                  ^^^^^^^^^^^^^^^^^^^
                                                  NEW: Required parameter
```

### Frontend Changes

#### 1. New Components

**StationSelector Component** (`components/StationSelector.jsx`)
- Dropdown selector for switching stations
- Shows station name with active indicator
- Saves selection to localStorage
- CSS styling matching radioawa design

#### 2. Context API

**StationContext** (`contexts/StationContext.jsx`)
- Global state management for stations
- Provides:
  - `stations` - Array of available stations
  - `currentStation` - Currently selected station
  - `changeStation(code)` - Switch station function
  - `loading` - Loading state
- Automatically fetches stations from API on mount
- Restores last selected station from localStorage

#### 3. Utility Functions

**stationService.js** (`services/stationService.js`)
```javascript
export async function fetchStations() {
  const response = await fetch('/api/stations');
  return response.json();
}
```

**stationStorage.js** (`utils/stationStorage.js`)
```javascript
export function getLastStation() { ... }
export function setLastStation(code) { ... }
```

#### 4. Modified Components

**App.jsx**
- Wrapped with `<StationProvider>`
- Added `<StationSelector>` in header

**RadioPlayer.jsx**
- Uses `currentStation.streamUrl` dynamically
- Recreates HLS player when station changes
- Stream URL now reactive to station changes

**NowPlaying.jsx**
- Uses `currentStation.metadataUrl` dynamically
- Uses `currentStation.albumArtUrl` dynamically
- Metadata polling updates when station changes

**SongRating.jsx**
- Passes `currentStation.code` to rating service
- Ratings are now station-specific
- Re-fetches ratings when station changes

**ratingService.js**
- Added `stationCode` parameter to all functions
- `submitRating(artist, title, userId, ratingType, stationCode)`
- `getRatingCounts(artist, title, userId, stationCode)`

## User Experience

### Station Switching Flow

1. **User clicks station selector** in app header
2. **Dropdown shows available stations** (ENGLISH, HINDI)
3. **User selects new station**
4. **System updates**:
   - Stream URL → Audio player switches to new stream
   - Metadata URL → Fetches now playing from new station
   - Album artwork → Displays artwork for new station
   - Ratings → Shows ratings for songs on new station
   - localStorage → Saves selection for next visit
5. **Playback continues** with new station content

### Data Isolation

- **Songs**: Each station has separate song library
  - "Artist - Song" on ENGLISH station ≠ same on HINDI station
  - Independent rating counts per station

- **Ratings**: Votes are station-specific
  - User can rate same song differently on different stations
  - Rate limits apply per station (20 votes/hour/IP per station)

- **Metadata**: Each station has unique now playing info
  - Different metadata sources
  - Different update frequencies possible

## Configuration

### Adding a New Station

#### 1. Database Insert

```sql
INSERT INTO stations (code, name, stream_url, metadata_url, album_art_url, is_active, display_order)
VALUES (
  'TAMIL',                                              -- Unique code
  'RadioAwa Tamil',                                     -- Display name
  'https://your-cdn.com/tamil/live.m3u8',              -- HLS stream
  'https://your-cdn.com/tamil/metadata.json',          -- Metadata
  'https://your-cdn.com/tamil/cover.jpg',              -- Album art
  true,                                                 -- Active
  3                                                     -- Display order
);
```

#### 2. No Code Changes Required

The system automatically:
- ✅ Fetches new station via API
- ✅ Shows in station selector dropdown
- ✅ Creates songs table entries when rated
- ✅ Applies rate limiting per station
- ✅ Isolates ratings to station

### Updating Station URLs

```sql
UPDATE stations
SET stream_url = 'https://new-cdn.com/live.m3u8',
    metadata_url = 'https://new-cdn.com/metadata.json',
    updated_at = CURRENT_TIMESTAMP
WHERE code = 'HINDI';
```

Frontend will pick up changes on next page load or station switch.

### Deactivating a Station

```sql
UPDATE stations
SET is_active = false
WHERE code = 'HINDI';
```

Station will no longer appear in selector (existing ratings preserved).

## Testing

### Manual Testing Checklist

- [x] Station selector appears in app header
- [x] Dropdown shows all active stations
- [x] Clicking station switches stream
- [x] Metadata updates for new station
- [x] Album artwork updates for new station
- [x] Ratings are station-specific
- [x] Rate limiting works per station
- [x] Last station is remembered on page reload
- [x] Switch during playback works smoothly
- [x] Multiple browser tabs sync station selection

### API Testing

```bash
# List all stations
curl http://localhost:8081/api/stations

# Submit rating for English station
curl -X POST http://localhost:8081/api/ratings \
  -H 'Content-Type: application/json' \
  -d '{
    "artist": "Test Artist",
    "title": "Test Song",
    "userId": "test-uuid",
    "ratingType": "THUMBS_UP",
    "stationCode": "ENGLISH"
  }'

# Get ratings for Hindi station
curl "http://localhost:8081/api/ratings/song?artist=Test&title=Song&stationCode=HINDI"

# Test rate limiting (20 votes/hour/IP per station)
for i in {1..25}; do
  curl -X POST http://localhost:8081/api/ratings \
    -H 'Content-Type: application/json' \
    -d '{"artist":"A","title":"B","userId":"'$i'","ratingType":"THUMBS_UP","stationCode":"ENGLISH"}'
  echo ""
done
# Should succeed for votes 1-20, fail for 21+
```

## Database Migration

The database was migrated using the following SQL script (`backend/src/main/resources/db/migration/add_stations.sql`):

```sql
-- Create stations table
CREATE TABLE IF NOT EXISTS stations (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    stream_url VARCHAR(500) NOT NULL,
    metadata_url VARCHAR(500) NOT NULL,
    album_art_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default stations
INSERT INTO stations (code, name, stream_url, metadata_url, is_active, display_order)
VALUES
  ('ENGLISH', 'RadioAwa English', 'https://d3d4yli4hf5bmh.cloudfront.net/hls/live.m3u8',
   'https://d3d4yli4hf5bmh.cloudfront.net/metadatav2.json', true, 1),
  ('HINDI', 'RadioAwa Hindi', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
   'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8', true, 2);

-- Add station_id to songs table
ALTER TABLE songs ADD COLUMN IF NOT EXISTS station_id BIGINT;
ALTER TABLE songs ADD CONSTRAINT fk_songs_station FOREIGN KEY (station_id) REFERENCES stations(id);

-- Create index for station-scoped song queries
CREATE INDEX IF NOT EXISTS idx_songs_station_artist_title ON songs(station_id, artist, title);

-- Migrate existing songs to ENGLISH station
UPDATE songs SET station_id = (SELECT id FROM stations WHERE code = 'ENGLISH') WHERE station_id IS NULL;

-- Make station_id required
ALTER TABLE songs ALTER COLUMN station_id SET NOT NULL;
```

## Future Enhancements

### Phase 1: Enhanced Station Management
- [ ] Admin UI for managing stations
- [ ] Station-specific themes/branding
- [ ] Station logos/images
- [ ] Station descriptions and schedules

### Phase 2: Advanced Features
- [ ] Schedule-based station rotation
- [ ] Geographic station selection
- [ ] Language preferences
- [ ] Genre tags for stations
- [ ] Station-specific playlists

### Phase 3: Analytics
- [ ] Listener count per station
- [ ] Popular songs per station
- [ ] Peak hours per station
- [ ] Station switching patterns

## Known Issues

1. **Hindi Station Stream URLs**: Currently using Mux test streams
   - **Impact**: Not actual Hindi music content
   - **Fix**: Update with actual Hindi radio stream URLs

2. **Album Artwork URL**: Hindi station uses metadata URL
   - **Impact**: May not have proper album art
   - **Fix**: Add dedicated album art endpoint for Hindi station

3. **No Offline Fallback**: If API fails, no stations load
   - **Impact**: App won't work if backend is down
   - **Fix**: Add hardcoded fallback station in frontend

## Performance Considerations

- **Database Queries**: All queries use indexes (station_id, artist, title)
- **API Calls**: Stations fetched once on app load, cached in context
- **Memory**: HLS player destroyed and recreated on station change (prevents memory leaks)
- **Network**: Only metadata polling continues per station (10s interval)

## Security Considerations

- **Rate Limiting**: Applied per station to prevent abuse
- **Input Validation**: Station codes validated against database
- **SQL Injection**: Prevented via JPA parameterized queries
- **XSS**: Station names sanitized in frontend rendering

## File Manifest

### Backend Files Modified/Created
```
backend/src/main/java/com/radioawa/
├── entity/
│   ├── Station.java                    [NEW]
│   ├── Song.java                       [MODIFIED - added station relationship]
│   └── Rating.java                     [UNCHANGED]
├── repository/
│   ├── StationRepository.java          [NEW]
│   ├── SongRepository.java             [MODIFIED - station-scoped queries]
│   └── RatingRepository.java           [MODIFIED - station-aware rate limits]
├── dto/
│   └── StationResponse.java            [NEW]
├── controller/
│   ├── StationController.java          [NEW]
│   └── RatingController.java           [MODIFIED - extract stationCode]
└── service/
    └── RatingService.java              [MODIFIED - station-scoped logic]

backend/src/main/resources/
└── db/migration/
    └── add_stations.sql                [NEW]
```

### Frontend Files Modified/Created
```
frontend/src/
├── components/
│   ├── StationSelector.jsx             [NEW]
│   ├── StationSelector.css             [NEW]
│   ├── RadioPlayer.jsx                 [MODIFIED - dynamic streamUrl]
│   ├── NowPlaying.jsx                  [MODIFIED - dynamic metadataUrl]
│   └── SongRating.jsx                  [MODIFIED - pass stationCode]
├── contexts/
│   └── StationContext.jsx              [NEW]
├── services/
│   ├── stationService.js               [NEW]
│   └── ratingService.js                [MODIFIED - add stationCode params]
├── utils/
│   └── stationStorage.js               [NEW]
└── App.jsx                             [MODIFIED - StationProvider wrapper]
```

## Conclusion

The multi-station feature successfully transforms radioawa from a single-stream application to a multi-station platform. The architecture is scalable, maintainable, and provides a solid foundation for future enhancements like admin management, station-specific branding, and advanced analytics.

The implementation follows best practices:
- ✅ Clean separation of concerns
- ✅ Database normalization
- ✅ RESTful API design
- ✅ React Context for state management
- ✅ Backward compatibility (existing English station works unchanged)
- ✅ Performance optimized (indexes, caching)
- ✅ User-friendly (persistent selection, smooth switching)

---

**Next Steps:**
1. Update Hindi station with actual radio stream URLs
2. Add dedicated album artwork endpoints for each station
3. Create admin dashboard for station management
4. Implement station-specific themes/branding
5. Add analytics for tracking station popularity

**Author**: Sujit K Singh
**Last Updated**: December 2024
