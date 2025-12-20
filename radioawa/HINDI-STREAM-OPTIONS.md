# Hindi Music Stream Options for RadioAwa

## Overview

Finding reliable, publicly accessible Hindi music HLS (.m3u8) streams can be challenging because:
- Many streams require authentication or referrer headers
- Stream URLs often expire or change
- Stations may not publicly document their streaming endpoints
- Some streams are region-locked to India

This guide provides multiple options and methods for finding Hindi music streams.

---

## Option 1: HLS Stream URLs (Recommended for RadioAwa)

### 🎵 Vividh Bharati (All India Radio)
**Type:** Government radio station
**Content:** Classic Hindi film songs, Bollywood music
**Quality:** Good
**Stream URL:**
```
https://vividhbharati-lh.akamaihd.net/i/vividhbharati_1@507811/index_1_a-p.m3u8?sd=10&rebase=on
```

**Alternative Vividh Bharati URLs:**
```
http://airhlspush.pc.cdn.bitgravity.com/httppush/hlspbaudio026/hlspbaudio026_Auto.m3u8
```

**Metadata:** No real-time metadata API available (will need to use simulated metadata like current setup)

---

### 🎶 Radio Mirchi - Mehfil-E-Ghazal
**Type:** Commercial radio (Ghazal and romantic Hindi songs)
**Content:** Ghazals, romantic Bollywood classics
**Quality:** Good
**Stream URL:**
```
https://mirchimahfil-lh.akamaihd.net/i/MirchiMehfl_1@120798/index_1_a-b.m3u8
```

**Metadata:** No real-time metadata API available

---

### 🙏 Shemaroo Bhakti Radio
**Type:** Devotional music
**Content:** Hindi bhajans, devotional songs
**Quality:** Good
**Stream URL:**
```
https://radiobhaktishopping.shemaroo.com/hariomradio/Stream1_aac/chunklist_w1854039901.m3u8
```

**Note:** This URL may expire as it appears to have a token in the filename (`w1854039901`)

**Metadata:** No real-time metadata API available

---

### 📻 FM Gold (All India Radio)
**Type:** Government radio
**Content:** Retro Hindi songs from 1950s-1990s
**Quality:** Good
**Stream URL:**
```
http://airfmgold-lh.akamaihd.net/i/fmgold_1@507591/master.m3u8
```

**Note:** May require specific headers or referrer to work

**Metadata:** No real-time metadata API available

---

## Option 2: HTTP/AAC Streams (Non-HLS)

If HLS doesn't work, these HTTP streams can also work with modern browsers:

### Radio City 91.1 FM
```
http://prclive1.listenon.in:9960/
```

### Radio City - Asha Bhosle
```
http://prclive1.listenon.in:8812/
```

### Radio City - Love Guru
```
http://prclive1.listenon.in:9938/
```

### Radio Mirchi (Various cities)
```
http://peridot.streamguys.com:7150/Mirchi
```

**Note:** HTTP streams work but HLS is preferred for better buffering and quality adaptation.

---

## Option 3: Using Internet Radio Directories

### Community Radio Browser API
The Radio Browser API provides access to thousands of radio stations worldwide, including Hindi stations:

**API Endpoint:**
```
https://de1.api.radio-browser.info/json/stations/byname/hindi
```

**Example stations available:**
- Radio City Hindi
- Big FM Hindi
- Mirchi Love
- Vividh Bharati

**How to use:**
1. Query the API to find Hindi stations
2. Extract the `url_resolved` field for the stream URL
3. Many stations provide m3u8/HLS streams

**API Documentation:** https://www.radio-browser.info/

---

## Option 4: Create Your Own Hindi Music Stream

If you can't find a reliable public stream, you can create your own:

### Using Icecast/Shoutcast Server

1. **Set up streaming server:**
   - Use Icecast2 (open source) or Shoutcast
   - Host on cloud provider (AWS, DigitalOcean, etc.)

2. **Create playlist:**
   - Compile Hindi music library
   - Use tools like Liquidsoap or Azuracast to stream

3. **Generate HLS stream:**
   - Icecast2 can generate HLS streams from source audio

**Pros:** Full control, always available, custom metadata
**Cons:** Requires licensing for music, hosting costs, technical setup

---

## How to Test and Update Stream URLs

### Step 1: Test a Stream URL

#### Using VLC Media Player:
```bash
vlc "https://vividhbharati-lh.akamaihd.net/i/vividhbharati_1@507811/index_1_a-p.m3u8?sd=10&rebase=on"
```

#### Using curl:
```bash
curl -I "YOUR_STREAM_URL"
# Should return HTTP 200 OK
```

#### Using ffprobe:
```bash
ffprobe "YOUR_STREAM_URL" 2>&1 | grep -i audio
```

#### In Browser:
Open Chrome DevTools → Network tab, paste URL in address bar

---

### Step 2: Update RadioAwa Database

Once you find a working stream URL:

```bash
# Connect to database
docker compose exec postgres psql -U radioawa -d radioawa

# Update stream URL
UPDATE stations
SET stream_url = 'YOUR_WORKING_STREAM_URL'
WHERE code = 'HINDI';

# Verify
SELECT code, name, stream_url FROM stations WHERE code = 'HINDI';

# Exit
\q
```

---

### Step 3: Keep Using Simulated Metadata (Recommended)

Since most Hindi radio streams don't provide real-time metadata APIs, **keep using the current MetadataProxyController** which provides:
- Simulated Hindi song metadata
- Rotating playlist of popular Bollywood songs
- Album artwork
- Recently played list

**OR** you can:
1. Remove the demo notice if you're using an actual Hindi stream
2. Make the metadata match the stream's typical content (e.g., "Classic Hindi Songs" if using Vividh Bharati)

---

## Recommended Approach for RadioAwa

Given the limitations, here's my recommendation:

### Best Option: Vividh Bharati + Current Metadata System

1. **Use Vividh Bharati stream** (Government station, most reliable):
```
https://vividhbharati-lh.akamaihd.net/i/vividhbharati_1@507811/index_1_a-p.m3u8?sd=10&rebase=on
```

2. **Keep the MetadataProxyController** with slight modifications:
   - Remove the "demo notice" banner
   - Update metadata to show "Classic Bollywood Hits" or "Vividh Bharati - Hindi Music"
   - Keep the simulated playlist (users will understand it's a general representation)

3. **Update database:**
```sql
UPDATE stations
SET
  stream_url = 'https://vividhbharati-lh.akamaihd.net/i/vividhbharati_1@507811/index_1_a-p.m3u8?sd=10&rebase=on',
  name = 'RadioAwa Hindi - Vividh Bharati'
WHERE code = 'HINDI';
```

4. **Modify MetadataProxyController** to remove demo notice:
   - Remove `is_demo: true`
   - Remove `demo_notice` field
   - Or update notice to: "Live Hindi music from Vividh Bharati"

---

## Alternative: Build Your Own Stream Aggregator

For the best user experience, consider:

1. **Aggregate multiple sources:**
   - Scrape metadata from Gaana.com, JioSaavn, or Spotify Web API
   - Match approximate "now playing" to actual Hindi radio content

2. **Use a music identification API:**
   - Audd.io API (identifies audio from streams)
   - ACRCloud (audio fingerprinting)
   - ShazamAPI

3. **Hybrid approach:**
   - Stream actual Hindi radio (Vividh Bharati)
   - Show curated "typical playlist" metadata
   - Add disclaimer: "Representing typical Hindi classics playing now"

---

## Sources

1. [GitHub - Indian Bollywood Radio Stream Links](https://github.com/sdbabhishek/Indian-Bollywood-online-Radio-Music-Stream-links)
2. [FMStream.org - India Radio Streams](https://fmstream.org/index.php?c=IND)
3. [GitHub - Internet Radio HQ URL Playlists](https://github.com/Pulham/Internet-Radio-HQ-URL-playlists)
4. [Radio City India Streaming](https://onlineradiofm.in/stations/city-91.1-fm)
5. [Radio Browser API](https://www.radio-browser.info/)

---

## Testing Checklist

Before deploying a new stream:

- [ ] Stream plays in VLC media player
- [ ] Stream plays in RadioAwa player (test in browser)
- [ ] Stream doesn't require authentication
- [ ] Stream works from your server's geographic location
- [ ] Stream is stable (test for 30+ minutes)
- [ ] Audio quality is acceptable
- [ ] No geo-blocking issues
- [ ] Legal to use/redistribute

---

## Need Help?

If you encounter issues with any stream URL:

1. **Check if stream is region-locked:** Use a VPN to test from different locations
2. **Inspect network requests:** Use browser DevTools to see actual errors
3. **Try with User-Agent header:** Some streams require specific user agents
4. **Contact station directly:** Ask for official streaming API documentation

---

**Last Updated:** 2025-12-20
**Tested From:** United States
