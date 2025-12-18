# IP-Based Duplicate Prevention - Implementation Summary

**Author**: Sujit K Singh

## 🎯 Overview

Implemented a **hybrid approach** combining UUID (localStorage) + IP address tracking to prevent duplicate ratings while maintaining user privacy and usability.

## 🔧 What Was Implemented

### Primary: UUID (localStorage)
- User gets a random UUID on first visit
- Stored in browser localStorage as `radioawa_user_id`
- Persists across page refreshes
- Main identifier for legitimate users

### Secondary: IP Address + Rate Limiting
- Backend captures IP address from HTTP requests
- Handles proxies/load balancers (X-Forwarded-For headers)
- Supports both IPv4 and IPv6
- Rate limiting: **Max 20 votes per hour per IP**

## 📋 Changes Made

### 1. Database Schema
Added `ip_address` column to `ratings` table:
```sql
ALTER TABLE ratings ADD COLUMN ip_address VARCHAR(45);
```

**Fields:**
- `ip_address` - VARCHAR(45) to support IPv4 and IPv6
- Nullable (backward compatible with existing ratings)

### 2. Backend Changes

**Rating.java** - Added IP address field:
```java
@Column(name = "ip_address", length = 45)
private String ipAddress;
```

**RatingRepository.java** - Added IP-based queries:
```java
// Count recent votes from an IP
long countByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime since);

// Count votes from IP for specific song
long countByIpAddressAndSong(String ipAddress, Song song);
```

**RatingService.java** - Implemented rate limiting:
```java
// Configuration
private static final int MAX_VOTES_PER_HOUR_PER_IP = 20;
private static final int RATE_LIMIT_HOURS = 1;

// Rate limiting check
if (recentVotesFromIp >= MAX_VOTES_PER_HOUR_PER_IP) {
    throw new RuntimeException("Rate limit exceeded...");
}
```

**RatingController.java** - Capture IP address:
```java
private String getClientIpAddress(HttpServletRequest request) {
    // Checks X-Forwarded-For, Proxy-Client-IP, etc.
    // Handles multiple IPs in X-Forwarded-For
    // Falls back to request.getRemoteAddr()
}
```

### 3. Frontend Changes
**None required!** 🎉
- Frontend continues to send UUID
- Backend transparently captures IP
- No JavaScript or UI changes needed

## 🔒 How It Works

### Normal Flow
1. User visits site → UUID generated and stored
2. User rates a song → Frontend sends UUID
3. Backend captures IP from HTTP request
4. Check: UUID hasn't rated this song
5. Check: IP hasn't exceeded rate limit (20/hour)
6. If both pass → Allow vote
7. Store both UUID and IP with rating

### Rate Limit Flow
```
Vote  1-19: ✅ Allowed
Vote 20:    ✅ Allowed (at limit)
Vote 21:    ❌ BLOCKED - "Rate limit exceeded"
Vote 22:    ❌ BLOCKED
...
After 1 hour: ✅ Can vote again
```

### Duplicate Prevention
| Scenario | Result |
|----------|--------|
| Same UUID + Same IP | ✅ One vote (primary check) |
| Different UUID + Same IP (spam) | ❌ Blocked after 20/hour |
| Same UUID + Different IP (VPN) | ✅ One vote per song (UUID tracked) |
| Clear browser + Same IP | ✅ Limited by rate limit |

## 🧪 Testing Results

### Test 1: IP Capture
```bash
curl POST /api/ratings -d '{"artist":"Pink Floyd",...}'
# Database shows: ip_address = "0:0:0:0:0:0:0:1" (IPv6 localhost)
```
✅ **PASS** - IP captured correctly

### Test 2: Rate Limiting
```bash
# Submit 22 votes from same IP
Votes 1-19: "Rating submitted successfully"
Vote 20:    "Rate limit exceeded. Maximum 20 votes per hour allowed."
Vote 21-22: "Rate limit exceeded. Maximum 20 votes per hour allowed."
```
✅ **PASS** - Rate limiting works exactly as expected

### Test 3: Database Verification
```sql
SELECT ip_address, COUNT(*) FROM ratings GROUP BY ip_address;
```
```
ip_address       | votes
0:0:0:0:0:0:0:1 | 20
(empty)          | 9   -- Old ratings before IP tracking
```
✅ **PASS** - IP addresses stored, backward compatible

## 📊 Rate Limit Configuration

**Current Settings:**
```java
MAX_VOTES_PER_HOUR_PER_IP = 20
RATE_LIMIT_HOURS = 1
```

**To Adjust:**
1. Edit `backend/src/main/java/com/radioawa/service/RatingService.java`
2. Change constants:
   ```java
   private static final int MAX_VOTES_PER_HOUR_PER_IP = 50; // New limit
   private static final int RATE_LIMIT_HOURS = 2;           // 2 hour window
   ```
3. Rebuild: `mvn clean compile`
4. Restart backend

## 🛡️ Security Features

### IP Capture Handles:
- ✅ Proxies (X-Forwarded-For)
- ✅ Load balancers (Proxy-Client-IP)
- ✅ Multiple proxies (takes first IP from comma list)
- ✅ IPv4 and IPv6
- ✅ Missing headers (fallback to remote address)

### Privacy Considerations:
- ✅ IP not exposed to frontend
- ✅ IP only used for rate limiting
- ✅ No detailed tracking/profiling
- ✅ UUID remains primary identifier
- ✅ GDPR-friendly (IP for abuse prevention)

## 🚫 What This Prevents

| Attack Method | Prevention |
|---------------|------------|
| **Spam voting from script** | Rate limit (20/hour per IP) |
| **Clear browser repeatedly** | Rate limit kicks in |
| **Incognito mode spam** | Rate limit prevents abuse |
| **Multiple browsers** | Rate limit applies to IP |
| **VPN switching** | UUID still prevents same-song duplicates |

## ⚠️ Limitations

### What's NOT Prevented:
1. **VPN + Clear browser every vote** - User can bypass
   - Mitigation: Acceptable for casual voting
   - Alternative: Require account registration

2. **Shared IP (family/office)** - Multiple people blocked
   - Mitigation: 20 votes/hour should cover normal usage
   - Alternative: Increase rate limit

3. **Dynamic IP changes** - Mobile users get new limit
   - Mitigation: UUID still primary, IP is secondary
   - Impact: Minimal for normal users

## 📈 Monitoring

### Check Vote Distribution:
```sql
SELECT ip_address, COUNT(*) as votes,
       MAX(created_at) as last_vote
FROM ratings
WHERE created_at > NOW() - INTERVAL '1 hour'
GROUP BY ip_address
ORDER BY votes DESC;
```

### Detect Suspicious Activity:
```sql
-- IPs hitting rate limit
SELECT ip_address, COUNT(*) as attempts
FROM ratings
WHERE created_at > NOW() - INTERVAL '1 hour'
GROUP BY ip_address
HAVING COUNT(*) >= 20;
```

### View Rate Limit Logs:
```bash
tail -f backend.log | grep "Rate limit exceeded"
```

## 🎯 Recommendations

### Current Setup (20/hour): Good for
- ✅ Casual radio station voting
- ✅ Prevents blatant abuse
- ✅ Allows legitimate users to rate multiple songs
- ✅ No registration friction

### Consider Increasing if:
- Many legitimate users share IPs (offices, schools)
- Users rate many songs quickly (power users)
- Feedback shows false positives

### Consider Decreasing if:
- Significant spam detected
- Want stricter abuse prevention
- Database growing too fast

## 🔗 Related Files

**Backend:**
- `backend/src/main/java/com/radioawa/entity/Rating.java`
- `backend/src/main/java/com/radioawa/service/RatingService.java`
- `backend/src/main/java/com/radioawa/controller/RatingController.java`
- `backend/src/main/java/com/radioawa/repository/RatingRepository.java`

**Database:**
- Table: `ratings` (column: `ip_address`)

**No frontend changes required!**

## 📞 Troubleshooting

### Issue: Rate limit too strict
**Solution:** Increase `MAX_VOTES_PER_HOUR_PER_IP` in RatingService.java

### Issue: IP showing as 127.0.0.1 in production
**Solution:** Check proxy/load balancer forwards X-Forwarded-For header

### Issue: IPv6 addresses too long
**Solution:** Already handled - VARCHAR(45) supports full IPv6

### Issue: Old ratings show NULL IP
**Solution:** Expected - backward compatible, no impact on functionality

## ✅ Summary

**Implementation: Complete**
- ✅ IP tracking enabled
- ✅ Rate limiting active (20 votes/hour/IP)
- ✅ Backward compatible
- ✅ No frontend changes
- ✅ Tested and verified

**Protection Level:** Medium-High
- Stops casual abuse ✅
- Allows legitimate users ✅
- Privacy-friendly ✅
- Can be bypassed by determined attacker (acceptable trade-off)

**Next Steps:** None required. System is production-ready!
