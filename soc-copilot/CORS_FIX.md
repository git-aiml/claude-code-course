# CORS Issue Fix - "Failed to fetch" Error

## 🐛 Problem

**Error in Frontend:** `Error: Failed to fetch`

**Symptom:** When clicking the "Refresh" button in the frontend, the application shows "Error: Failed to fetch" and cannot load alerts from the backend.

## 🔍 Root Cause: CORS Configuration Mismatch

### What is CORS?
CORS (Cross-Origin Resource Sharing) is a security feature that prevents websites from making requests to a different domain/port than the one serving the page.

### The Issue
- **Frontend running on:** `http://localhost:5173` (Vite default port)
- **Backend configured for:** `http://localhost:3000` only
- **Result:** Browser blocks the request → "Failed to fetch"

### Technical Details

The backend had two places with incorrect CORS configuration:

**1. AlertController.java (Line 16)**
```java
// BEFORE (Wrong)
@CrossOrigin(origins = "http://localhost:3000")

// AFTER (Fixed)
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
```

**2. SecurityConfig.java (Line 35)**
```java
// BEFORE (Wrong)
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

// AFTER (Fixed)
configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
```

## ✅ Solution Applied

### Files Changed
1. `/backend/src/main/java/com/aisoc/copilot/controller/AlertController.java`
   - Updated `@CrossOrigin` annotation to include `http://localhost:5173`

2. `/backend/src/main/java/com/aisoc/copilot/config/SecurityConfig.java`
   - Updated CORS configuration to allow both ports 3000 and 5173

### Why Both Ports?
- **Port 3000:** Standard React/Create-React-App port
- **Port 5173:** Vite development server port (what we're using)
- Supporting both provides flexibility for different frontend setups

## 🧪 Verification

### 1. Check Services Running
```bash
./check-status.sh
```

Expected output:
- ✓ Backend running on port 8080
- ✓ Frontend running on port 5173
- ✓ Database running on port 5432

### 2. Test API Directly
```bash
curl http://localhost:8080/api/alerts
```

Should return JSON array of alerts.

### 3. Test Frontend
1. Open browser: http://localhost:5173
2. Click "Refresh" button
3. Should see alerts loading successfully
4. No "Failed to fetch" error

## 📊 Current Data

The database currently has 5 test alerts:
1. Test Security Alert (HIGH)
2. Malware Detection - Quarantined (CRITICAL)
3. Brute Force Attack (HIGH)
4. Port Scan Detected (MEDIUM)
5. Test Alert with Minimal Fields (LOW)

## 🎯 How to Apply This Fix

If you encounter this issue again:

### Step 1: Identify the Problem
- Check browser console (F12) for CORS errors
- Look for "Access-Control-Allow-Origin" errors
- Note which port your frontend is running on

### Step 2: Update Backend CORS
Update both files to include your frontend port:
- `AlertController.java`: `@CrossOrigin` annotation
- `SecurityConfig.java`: `setAllowedOrigins()` method

### Step 3: Restart Backend
```bash
./stop-backend.sh
./start-backend.sh
# or use start-dev.sh for both services
```

### Step 4: Test
- Refresh frontend page
- Should now load data successfully

## 🔧 For Production

**Important:** In production, you should:

1. **Use specific origins** (not wildcard)
   ```java
   configuration.setAllowedOrigins(Arrays.asList(
       "https://yourdomain.com",
       "https://app.yourdomain.com"
   ));
   ```

2. **Use environment variables**
   ```java
   configuration.setAllowedOrigins(
       Arrays.asList(environment.getProperty("cors.allowed.origins").split(","))
   );
   ```

3. **Never use `*` in production**
   ```java
   // DON'T DO THIS IN PRODUCTION
   configuration.setAllowedOrigins(Arrays.asList("*"));
   ```

## 📚 Related Information

### Browser Console Errors (Before Fix)
```
Access to fetch at 'http://localhost:8080/api/alerts' from origin
'http://localhost:5173' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

### Network Tab (Before Fix)
- Request to `/api/alerts` shows as failed (red)
- Status: CORS error
- No response data

### Network Tab (After Fix)
- Request to `/api/alerts` shows as successful (green)
- Status: 200 OK
- Response data: JSON array of alerts

## 🚀 Quick Commands

### If Frontend Shows "Failed to fetch"
```bash
# 1. Check what's running
./check-status.sh

# 2. Test API directly
curl http://localhost:8080/api/alerts

# 3. If API works but frontend doesn't - CORS issue
# Check browser console for CORS errors

# 4. Restart backend after CORS fix
./stop-backend.sh
./start-backend.sh
```

### Add Test Data
```bash
# Using Postman collection
# Import: backend/Alerts-API-Postman-Collection.json
# Run: Create Alert requests

# Or use curl
curl -X POST http://localhost:8080/api/alerts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Alert",
    "description": "Testing CORS fix",
    "severity": "LOW",
    "status": "NEW"
  }'
```

## ✅ Success Indicators

After applying the fix:
- ✅ Frontend loads without errors
- ✅ "Refresh" button works correctly
- ✅ Alerts display in the UI
- ✅ No CORS errors in browser console
- ✅ Network tab shows successful API calls

## 🎓 Key Takeaways

1. **CORS is a browser security feature** - It prevents unauthorized cross-origin requests
2. **Backend must explicitly allow frontend origins** - No CORS config = blocked requests
3. **Port numbers matter** - `localhost:5173` ≠ `localhost:3000`
4. **Always restart backend after config changes** - Java/Spring Boot needs restart to pick up changes
5. **Check browser console first** - Most helpful for debugging frontend issues
