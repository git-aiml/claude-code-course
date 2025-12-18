# Radioawa API - Postman Collection Guide

**Author**: Sujit K Singh

## 📦 Importing the Collection

### Method 1: Import File
1. Open Postman
2. Click **Import** button (top left)
3. Drag and drop `backend/radioawa-api-collection.postman.json` or click **Choose Files**
4. Click **Import**

### Method 2: Import from URL
If you've uploaded the file to a repository:
```
https://raw.githubusercontent.com/your-repo/radioawa/main/radioawa-api-collection.postman.json
```

## 🔧 Configuration

### Collection Variables
The collection comes with pre-configured variables:

| Variable | Default Value | Description |
|----------|--------------|-------------|
| `baseUrl` | `http://localhost:8081` | Backend API base URL |
| `userId` | Auto-generated UUID | User identifier for rating operations |

### Changing the Base URL
If your backend is running on a different port or host:
1. Click on the collection name
2. Go to **Variables** tab
3. Update `baseUrl` to your backend URL
4. Click **Save**

### User ID Behavior
- A random UUID is automatically generated when you first use the collection
- This simulates a unique user across all requests
- To test with multiple users, manually change the `userId` variable

## 📋 Available Endpoints

### 1. Health Check
**GET** `/api/health`

Check if the backend service is running.

**Example Response:**
```json
{
  "status": "UP",
  "service": "Radioawa Backend",
  "timestamp": 1734513766000
}
```

---

### 2. Submit Thumbs Up Rating
**POST** `/api/ratings`

Submit a thumbs up rating for a song.

**Request Body:**
```json
{
  "artist": "The Beatles",
  "title": "Hey Jude",
  "userId": "{{userId}}",
  "ratingType": "THUMBS_UP"
}
```

**Response:**
```json
{
  "songId": 1,
  "artist": "The Beatles",
  "title": "Hey Jude",
  "thumbsUpCount": 1,
  "thumbsDownCount": 0,
  "userRating": "THUMBS_UP",
  "message": "Rating submitted successfully"
}
```

---

### 3. Submit Thumbs Down Rating
**POST** `/api/ratings`

Submit a thumbs down rating (or change from thumbs up).

**Request Body:**
```json
{
  "artist": "The Beatles",
  "title": "Hey Jude",
  "userId": "{{userId}}",
  "ratingType": "THUMBS_DOWN"
}
```

---

### 4. Get Rating Counts (With User)
**GET** `/api/ratings/song?artist={artist}&title={title}&userId={userId}`

Get rating counts including the user's current vote.

**Query Parameters:**
- `artist` (required) - Artist name
- `title` (required) - Song title
- `userId` (optional) - User UUID to check their vote

**Response:**
```json
{
  "songId": 1,
  "artist": "The Beatles",
  "title": "Hey Jude",
  "thumbsUpCount": 5,
  "thumbsDownCount": 2,
  "userRating": "THUMBS_UP"
}
```

---

### 5. Get Rating Counts (Without User)
**GET** `/api/ratings/song?artist={artist}&title={title}`

Get only aggregate rating counts without user-specific data.

**Response:**
```json
{
  "songId": 1,
  "artist": "The Beatles",
  "title": "Hey Jude",
  "thumbsUpCount": 5,
  "thumbsDownCount": 2,
  "userRating": null
}
```

## 🧪 Testing Scenarios

### Scenario 1: Rate a New Song
1. Run **Submit Thumbs Up Rating**
2. Observe: New song created, count = 1
3. Run same request again
4. Observe: Idempotent - count stays 1, message says "Rating already submitted"

### Scenario 2: Change Your Vote
1. Run **Submit Thumbs Up Rating**
2. Run **Submit Thumbs Down Rating** (same song, same user)
3. Observe: thumbsUpCount decreases, thumbsDownCount increases

### Scenario 3: Multiple Users Rating
1. Rate a song with default `{{userId}}`
2. Click collection → Variables → Change `userId` to a new UUID
3. Rate the same song again
4. Observe: Both ratings count towards total

### Scenario 4: Check Unrated Song
1. Run **Get Rating for Unrated Song**
2. Observe: Returns 0 counts, no songId

## 🎯 Tips

### Test with Multiple Users
```javascript
// In Postman Pre-request Script
pm.collectionVariables.set('userId', pm.variables.replaceIn('{{$randomUUID}}'));
```

### Test Special Characters
Songs with quotes, apostrophes work fine:
```json
{
  "artist": "Guns N' Roses",
  "title": "Sweet Child O' Mine"
}
```

### Rate Currently Playing Song
1. Open frontend at http://localhost:5171
2. Note the artist and title
3. Use those values in Postman requests

## 🔍 Troubleshooting

### Connection Refused
- ✅ Check backend is running: `http://localhost:8081/api/health`
- ✅ Verify `baseUrl` variable is correct
- ✅ Check firewall settings

### 400 Bad Request
- ✅ Ensure JSON body is valid
- ✅ Check all required fields are present: artist, title, userId, ratingType
- ✅ Verify ratingType is either "THUMBS_UP" or "THUMBS_DOWN"

### 500 Internal Server Error
- ✅ Check backend logs: `tail -f backend.log`
- ✅ Verify database is running
- ✅ Check database connection settings

### Rating Not Persisting
- ✅ Check database: `psql -U radioawa -d radioawa -c "SELECT * FROM ratings;"`
- ✅ Verify userId is consistent across requests

## 📊 Database Queries

### View All Ratings
```sql
SELECT r.id, s.artist, s.title, r.user_id, r.rating_type, r.created_at
FROM ratings r
JOIN songs s ON r.song_id = s.id
ORDER BY r.created_at DESC;
```

### View Song Totals
```sql
SELECT artist, title, thumbs_up_count, thumbs_down_count
FROM songs
ORDER BY (thumbs_up_count + thumbs_down_count) DESC;
```

### Find User's Ratings
```sql
SELECT s.artist, s.title, r.rating_type
FROM ratings r
JOIN songs s ON r.song_id = s.id
WHERE r.user_id = 'your-uuid-here';
```

## 🚀 Advanced Usage

### Environment Variables
Create separate environments for dev/staging/production:

**Development**
```
baseUrl: http://localhost:8081
```

**Production**
```
baseUrl: https://api.radioawa.com
```

### Automated Testing
Use Postman Tests tab to add assertions:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has rating counts", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('thumbsUpCount');
    pm.expect(jsonData).to.have.property('thumbsDownCount');
});
```

### Collection Runner
Run all requests sequentially:
1. Click collection name → **Run**
2. Select all requests
3. Click **Run Radioawa API Collection**

## 📝 Notes

- **Idempotent**: Submitting same rating multiple times = no change
- **Vote Changing**: Allowed - switch between up/down
- **User Identity**: UUID-based, stored in localStorage on frontend
- **Song Identity**: Unique by artist + title combination
- **Case Sensitive**: "The Beatles" ≠ "the beatles"

## 🔗 Related Files

- **Backend**: `backend/src/main/java/com/radioawa/controller/RatingController.java`
- **Frontend**: `frontend/src/services/ratingService.js`
- **Database**: Tables `songs` and `ratings`

## 📞 Support

For issues or questions:
- Check logs: `tail -f backend.log`
- Check database: `./db-cli.sh`
- Backend health: http://localhost:8081/api/health
- Frontend: http://localhost:5171
