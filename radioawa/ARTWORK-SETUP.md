# Album Artwork Setup Guide

The Now Playing widget displays album artwork for the currently playing track.

## Default Behavior

By default, the widget shows a **beautiful gradient placeholder** with a music note icon when no artwork API is configured. This looks great and matches the radioawa branding!

## Optional: Enable Real Album Artwork (Last.fm API)

If you want to display real album artwork fetched from Last.fm, follow these steps:

### Step 1: Get a Last.fm API Key (Free)

1. Go to https://www.last.fm/api/account/create
2. Sign up or log in to Last.fm
3. Fill in the application details:
   - **Application name**: radioawa
   - **Application description**: Personal radio streaming player
   - **Application homepage**: http://localhost:5171
   - **Callback URL**: (leave empty)
4. Click "Submit"
5. Copy your **API Key** (NOT the shared secret)

### Step 2: Add Your API Key to the Code

1. Open: `frontend/src/components/NowPlaying.jsx`
2. Find this line (around line 16):
   ```javascript
   const LASTFM_API_KEY = 'YOUR_API_KEY_HERE' // Replace with your key
   ```
3. Replace `YOUR_API_KEY_HERE` with your actual API key:
   ```javascript
   const LASTFM_API_KEY = 'abc123your_actual_key_here'
   ```
4. Save the file

### Step 3: Restart the Frontend

```bash
# Stop the frontend (Ctrl+C in frontend terminal)
# Or stop all:
./stop-all.sh

# Start again:
./start-all.sh
```

### Step 4: Test It

1. Open http://localhost:5171
2. Click Play
3. The Now Playing widget should now display real album artwork!

## How It Works

- **Without API Key**: Shows a beautiful gradient placeholder with music icon
- **With API Key**:
  - Fetches album artwork from Last.fm
  - Falls back to placeholder if artwork not found
  - Auto-updates every 10 seconds with new tracks

## Troubleshooting

### Artwork not showing even with API key?

1. **Check the browser console** (F12 → Console tab)
2. Look for errors related to Last.fm API
3. Common issues:
   - Invalid API key
   - Track/artist not found in Last.fm database
   - Network/CORS issues

### Artwork loads slowly?

- This is normal! Last.fm API needs to:
  1. Look up the track
  2. Find the album
  3. Fetch the image
- Placeholder shows while loading

### Want to use a different service?

You can modify the `fetchArtwork` function in `NowPlaying.jsx` to use:
- Spotify API
- MusicBrainz API
- Deezer API
- Your own artwork server

## Alternative: Host Your Own Artwork

If you want to serve artwork from your own server:

1. Create an artwork folder structure:
   ```
   /artwork/
     ├── artist1-album1.jpg
     ├── artist2-album2.jpg
     └── ...
   ```

2. Modify `NowPlaying.jsx` to construct URLs:
   ```javascript
   const artworkUrl = `/artwork/${artist}-${album}.jpg`
   ```

3. Serve the folder from your backend or CDN

## Privacy Note

When using Last.fm API:
- Your radioawa player sends track/artist names to Last.fm
- Last.fm may log these requests
- No personal user data is sent
- Consider this when playing private/sensitive content

---

**Recommended**: Keep the default placeholder unless you specifically need real album artwork. The placeholder looks great and protects your privacy!
