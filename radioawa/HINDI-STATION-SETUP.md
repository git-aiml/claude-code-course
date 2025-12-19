# Hindi Station Setup Guide

**Author**: Sujit K Singh
**Date**: December 2024
**Status**: Complete and Working

## Overview

Successfully configured **RadioAwa Hindi** station with:
- ✅ Live Hindi radio stream (All India Radio - Vividh Bharati)
- ✅ Simulated metadata with rotating Bollywood songs
- ✅ Full integration with rating system
- ✅ Isolated from English station

## What Was Implemented

### 1. Metadata Proxy Service

Created a backend controller that simulates metadata for Hindi station:

**File**: `backend/src/main/java/com/radioawa/controller/MetadataProxyController.java`

**Features**:
- 15 popular Bollywood songs in rotation
- Automatic song change every 4 minutes
- Recently played history (5 songs)
- REST API endpoints for metadata
- Compatible with frontend NowPlaying component

**Endpoints**:
```bash
# Get current song metadata
GET http://localhost:8081/api/metadata/hindi

# Get album artwork
GET http://localhost:8081/api/metadata/hindi/artwork

# Manually advance to next song (testing)
POST http://localhost:8081/api/metadata/hindi/next

# View full playlist
GET http://localhost:8081/api/metadata/hindi/playlist
```

### 2. Live Radio Stream

**Source**: All India Radio (AIR) - Vividh Bharati

**Stream Details**:
- **URL**: `https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8`
- **Format**: HLS (HTTP Live Streaming)
- **Type**: Live radio broadcast
- **Content**: Hindi music and programs
- **Quality**: Good audio quality
- **Reliability**: Government-operated, stable

**Why AIR Vividh Bharati?**
- Free and publicly accessible
- Official government radio station
- Reliable uptime
- HLS format compatible with our player
- Actual Hindi content (not test streams)

### 3. Database Configuration

Updated Hindi station in database:

```sql
UPDATE stations SET
  stream_url = 'https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8',
  metadata_url = 'http://localhost:8081/api/metadata/hindi',
  is_active = true
WHERE code = 'HINDI';
```

**Current Station Configuration**:

| Field | Value |
|-------|-------|
| Code | HINDI |
| Name | RadioAwa Hindi |
| Stream URL | https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8 |
| Metadata URL | http://localhost:8081/api/metadata/hindi |
| Active | true |
| Display Order | 2 |

## How It Works

### Stream Flow

```
1. User selects "RadioAwa Hindi" from station selector
         ↓
2. Frontend requests metadata from backend
         ↓
3. MetadataProxyController returns current song info
   - Artist: "Arijit Singh"
   - Title: "Tum Hi Ho"
   - Album: "Aashiqui 2"
   - Recently Played: [5 previous songs]
         ↓
4. Frontend starts playing HLS stream from AIR
         ↓
5. Every 10 seconds, metadata is refreshed
         ↓
6. Every 4 minutes, song rotates to next in playlist
         ↓
7. User can rate songs (isolated to HINDI station)
```

### Metadata Simulation

Since AIR doesn't provide song-level metadata via API, we simulate it:

**Playlist** (15 songs rotating):
1. Arijit Singh - "Tum Hi Ho" (Aashiqui 2)
2. Shreya Ghoshal - "Sunn Raha Hai" (Aashiqui 2)
3. Atif Aslam - "Jeene Laga Hoon" (Ramaiya Vastavaiya)
4. Arijit Singh - "Chahun Main Ya Naa" (Aashiqui 2)
5. Mohit Chauhan - "Tum Se Hi" (Jab We Met)
6. Shreya Ghoshal - "Teri Meri" (Bodyguard)
7. Arijit Singh - "Channa Mereya" (Ae Dil Hai Mushkil)
8. Neha Kakkar - "Aankh Marey" (Simmba)
9. Armaan Malik - "Bol Do Na Zara" (Azhar)
10. Atif Aslam - "Pehli Nazar Mein" (Race)
11. Arijit Singh - "Ae Dil Hai Mushkil" (Ae Dil Hai Mushkil)
12. Shreya Ghoshal - "Deewani Mastani" (Bajirao Mastani)
13. Arijit Singh - "Raabta" (Agent Vinod)
14. Neha Kakkar - "Dilbar" (Satyameva Jayate)
15. Sonu Nigam - "Abhi Mujh Mein Kahin" (Agneepath)

**Song Duration**: 4 minutes (configurable in `MetadataProxyController.java`)

## Testing

### Test the Stream URL

```bash
# Check if stream is accessible
curl -I "https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8"
# Should return: HTTP/2 200

# Download playlist
curl "https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8"
# Should return: #EXTM3U playlist
```

### Test the Metadata Proxy

```bash
# Get current metadata
curl http://localhost:8081/api/metadata/hindi | python3 -m json.tool

# Expected response:
{
    "artist": "Arijit Singh",
    "title": "Tum Hi Ho",
    "album": "Aashiqui 2",
    "timestamp": "2025-12-19T08:02:34.764521",
    "prev_artist_1": "Sonu Nigam",
    "prev_title_1": "Abhi Mujh Mein Kahin",
    ...
}
```

### Test the Stations API

```bash
# List all stations
curl http://localhost:8081/api/stations | python3 -m json.tool

# Should show both ENGLISH and HINDI stations
```

### Test Rating System

```bash
# Submit rating for Hindi song
curl -X POST http://localhost:8081/api/ratings \
  -H 'Content-Type: application/json' \
  -d '{
    "artist": "Arijit Singh",
    "title": "Tum Hi Ho",
    "userId": "test-user",
    "ratingType": "THUMBS_UP",
    "stationCode": "HINDI"
  }'

# Get rating counts
curl "http://localhost:8081/api/ratings/counts?artist=Arijit+Singh&title=Tum+Hi+Ho&stationCode=HINDI&userId=test-user"
```

## Using the Application

### Step 1: Access the Application

Open your browser:
```
http://localhost:5171
```

### Step 2: Select Hindi Station

1. Look at the top-right of the app header
2. Click on the **station selector dropdown**
3. Select **"RadioAwa Hindi"**

### Step 3: Start Playing

1. Click the **Play button**
2. Audio will start streaming from All India Radio
3. Metadata will show current "song" from our rotating playlist

### Step 4: Rate Songs

1. See the current song displayed in "Now Playing"
2. Click **thumbs up** or **thumbs down** to rate
3. Ratings are saved to database with `HINDI` station code
4. Different from English station ratings (isolated)

## Customization

### Change Song Rotation Speed

Edit `MetadataProxyController.java`:

```java
private static final int SONG_DURATION_MINUTES = 4; // Change this value
```

Restart backend:
```bash
cd backend && mvn spring-boot:run
```

### Add More Songs to Playlist

Edit `MetadataProxyController.java`:

```java
private static final List<Map<String, String>> HINDI_SONGS = Arrays.asList(
    createSong("Artist Name", "Song Title", "Album Name"),
    // Add more songs here...
);
```

### Use Different Hindi Radio Stream

**Option 1: Bollywood Stream (Zeno.FM)**
```sql
UPDATE stations SET
  stream_url = 'http://stream.zeno.fm/f3wvbbqmdg8uv'
WHERE code = 'HINDI';
```

**Option 2: Your Own Stream**
If you have your own Hindi radio stream:
```sql
UPDATE stations SET
  stream_url = 'https://your-stream-url.com/live.m3u8'
WHERE code = 'HINDI';
```

## Troubleshooting

### Stream Not Playing

**Problem**: Audio doesn't start when Hindi station is selected

**Solutions**:
1. Check if stream URL is accessible:
   ```bash
   curl -I "https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8"
   ```
2. Check browser console for errors (F12 → Console tab)
3. Try a different browser (Chrome/Firefox/Safari)
4. Clear browser cache and reload

### Metadata Not Updating

**Problem**: Song info doesn't change

**Solutions**:
1. Check backend is running:
   ```bash
   curl http://localhost:8081/api/health
   ```
2. Check metadata endpoint:
   ```bash
   curl http://localhost:8081/api/metadata/hindi
   ```
3. Check backend logs:
   ```bash
   tail -f backend.log
   ```
4. Restart backend:
   ```bash
   ./stop-all.sh && ./start-all.sh
   ```

### Ratings Not Saving

**Problem**: Thumbs up/down doesn't work

**Solutions**:
1. Check browser console for errors
2. Verify stationCode is being sent:
   ```bash
   # Check Network tab in DevTools (F12)
   # POST to /api/ratings should include "stationCode": "HINDI"
   ```
3. Check database:
   ```sql
   SELECT * FROM ratings
   WHERE song_id IN (
     SELECT id FROM songs WHERE station_id = (
       SELECT id FROM stations WHERE code = 'HINDI'
     )
   );
   ```

### Station Selector Not Showing

**Problem**: Can't see station selector dropdown

**Solutions**:
1. Check if StationSelector component is loaded:
   ```bash
   # View source in browser
   # Should see <select> element in header
   ```
2. Refresh frontend:
   ```bash
   # Frontend should auto-reload with Vite HMR
   # Or manually refresh: Cmd+R (Mac) / Ctrl+R (Windows)
   ```
3. Check frontend logs:
   ```bash
   tail -f frontend.log
   ```

## Advanced: Get Real Metadata from AIR

If you want actual song info from All India Radio stream:

### Option 1: Parse from Stream

AIR sometimes includes metadata in the stream itself:

```bash
# Install ffmpeg
brew install ffmpeg  # macOS
sudo apt install ffmpeg  # Linux

# Extract metadata
ffprobe -v quiet -print_format json -show_format \
  "https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8"
```

### Option 2: Scrape AIR Website

Create a web scraper to get now playing info:

```java
// Add to MetadataProxyController.java

private String fetchAIRMetadata() {
    try {
        // Scrape https://www.allindiaradio.gov.in/
        // Parse now playing information
        // Return artist/title
    } catch (Exception e) {
        // Fallback to simulated metadata
    }
}
```

### Option 3: Use Icecast Metadata

If AIR provides Icecast metadata:

```bash
curl -H "Icy-MetaData: 1" "https://air.pc.cdn.bitgravity.com/air/live/pbaudio043/playlist.m3u8"
```

## Alternative Hindi Radio Streams

If AIR stream stops working, try these alternatives:

### 1. Radio City 91.1 FM (if available)
```sql
UPDATE stations SET stream_url = 'http://stream.radiocity.in/city' WHERE code = 'HINDI';
```

### 2. Zeno.FM Bollywood Stream
```sql
UPDATE stations SET stream_url = 'http://stream.zeno.fm/f3wvbbqmdg8uv' WHERE code = 'HINDI';
```

### 3. Set Up Your Own Stream

Use Icecast + Liquidsoap to create your own Hindi radio station:

```bash
# Install Icecast
brew install icecast  # macOS
sudo apt install icecast2  # Linux

# Install Liquidsoap
brew install liquidsoap  # macOS
sudo apt install liquidsoap  # Linux

# Create stream from MP3 folder
liquidsoap 'output.icecast(%mp3, host="localhost", port=8000, password="hackme", mount="hindi", playlist("/path/to/hindi-songs"))'
```

Then update database:
```sql
UPDATE stations SET stream_url = 'http://localhost:8000/hindi' WHERE code = 'HINDI';
```

## Monitoring

### Check Current Song

```bash
curl -s http://localhost:8081/api/metadata/hindi | jq '.artist, .title, .album'
```

### View Playlist

```bash
curl -s http://localhost:8081/api/metadata/hindi/playlist | python3 -m json.tool
```

### Manually Change Song (Testing)

```bash
curl -X POST http://localhost:8081/api/metadata/hindi/next
```

## Summary

**What's Working:**
- ✅ Live Hindi radio stream (All India Radio)
- ✅ Simulated metadata with 15 Bollywood songs
- ✅ Song rotation every 4 minutes
- ✅ Recently played history
- ✅ Rating system (station-isolated)
- ✅ Station switching (English ⟷ Hindi)
- ✅ Persistent station selection

**What's Simulated:**
- ⚠️ Metadata (not real-time from AIR stream)
- ⚠️ Album artwork (placeholder)

**Future Enhancements:**
- [ ] Real-time metadata from AIR stream
- [ ] Actual album artwork from MusicBrainz API
- [ ] More Hindi radio station options
- [ ] Schedule-based programming

## Need Help?

Common questions:

**Q: Is the stream legal to use?**
A: All India Radio (AIR) is publicly accessible. For commercial use, contact AIR for licensing.

**Q: Can I add more songs to the metadata?**
A: Yes! Edit `HINDI_SONGS` list in `MetadataProxyController.java` and restart backend.

**Q: Can I change the song duration?**
A: Yes! Change `SONG_DURATION_MINUTES` constant in `MetadataProxyController.java`.

**Q: The stream is slow/buffering?**
A: AIR stream quality depends on your internet and their server load. Try alternative streams.

**Q: Can I use this in production?**
A: The simulated metadata is fine for testing. For production, implement real metadata scraping or use licensed music services.

---

**Enjoy your Hindi radio station!** 🎵

**Author**: Sujit K Singh
**Last Updated**: December 2024
