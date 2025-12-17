# REST API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
Currently using basic authentication (development mode):
- Username: `admin`
- Password: `admin123`

## Common Headers
```
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

---

## Alerts API

Base path: `/api/alerts`

### Data Model

#### Alert Object
```json
{
  "id": 1,
  "title": "Suspicious Login Attempt",
  "description": "Multiple failed login attempts detected from unknown IP",
  "severity": "HIGH",
  "status": "NEW",
  "sourceIp": "192.168.1.100",
  "destinationIp": "10.0.0.50",
  "createdAt": "2025-12-17T10:30:00",
  "updatedAt": "2025-12-17T10:30:00"
}
```

#### Field Descriptions

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | Long | Auto-generated | Unique identifier |
| `title` | String | Yes | Alert title (cannot be blank) |
| `description` | String | No | Detailed description of the alert |
| `severity` | String | Yes | Alert severity level |
| `status` | String | Yes | Current status (defaults to "NEW") |
| `sourceIp` | String | No | Source IP address |
| `destinationIp` | String | No | Destination IP address |
| `createdAt` | DateTime | Auto-generated | Timestamp when alert was created |
| `updatedAt` | DateTime | Auto-generated | Timestamp when alert was last updated |

#### Common Severity Values
- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

#### Common Status Values
- `NEW` (default)
- `INVESTIGATING`
- `RESOLVED`
- `CLOSED`
- `FALSE_POSITIVE`

---

## Endpoints

### 1. Get All Alerts

Retrieve a list of all alerts.

**Endpoint:** `GET /api/alerts`

**Request:**
```bash
curl -X GET http://localhost:8080/api/alerts \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Suspicious Login Attempt",
    "description": "Multiple failed login attempts detected",
    "severity": "HIGH",
    "status": "NEW",
    "sourceIp": "192.168.1.100",
    "destinationIp": "10.0.0.50",
    "createdAt": "2025-12-17T10:30:00",
    "updatedAt": "2025-12-17T10:30:00"
  },
  {
    "id": 2,
    "title": "Port Scan Detected",
    "description": "Systematic port scanning activity",
    "severity": "MEDIUM",
    "status": "INVESTIGATING",
    "sourceIp": "203.0.113.42",
    "destinationIp": "10.0.0.51",
    "createdAt": "2025-12-17T11:00:00",
    "updatedAt": "2025-12-17T11:15:00"
  }
]
```

---

### 2. Get Alert by ID

Retrieve a specific alert by its ID.

**Endpoint:** `GET /api/alerts/{id}`

**Path Parameters:**
- `id` (Long, required) - The alert ID

**Request:**
```bash
curl -X GET http://localhost:8080/api/alerts/1 \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Suspicious Login Attempt",
  "description": "Multiple failed login attempts detected",
  "severity": "HIGH",
  "status": "NEW",
  "sourceIp": "192.168.1.100",
  "destinationIp": "10.0.0.50",
  "createdAt": "2025-12-17T10:30:00",
  "updatedAt": "2025-12-17T10:30:00"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2025-12-17T10:30:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/alerts/999"
}
```

---

### 3. Create Alert

Create a new alert.

**Endpoint:** `POST /api/alerts`

**Request Body:**
```json
{
  "title": "Malware Detection",
  "description": "Trojan detected in downloaded file",
  "severity": "CRITICAL",
  "status": "NEW",
  "sourceIp": "185.220.101.50",
  "destinationIp": "10.0.0.100"
}
```

**Required Fields:**
- `title` (cannot be blank)
- `severity`
- `status`

**Request:**
```bash
curl -X POST http://localhost:8080/api/alerts \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "title": "Malware Detection",
    "description": "Trojan detected in downloaded file",
    "severity": "CRITICAL",
    "status": "NEW",
    "sourceIp": "185.220.101.50",
    "destinationIp": "10.0.0.100"
  }'
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "title": "Malware Detection",
  "description": "Trojan detected in downloaded file",
  "severity": "CRITICAL",
  "status": "NEW",
  "sourceIp": "185.220.101.50",
  "destinationIp": "10.0.0.100",
  "createdAt": "2025-12-17T12:00:00",
  "updatedAt": "2025-12-17T12:00:00"
}
```

**Validation Error:** `400 Bad Request`
```json
{
  "timestamp": "2025-12-17T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "title",
      "message": "must not be blank"
    }
  ]
}
```

---

### 4. Update Alert

Update an existing alert by ID.

**Endpoint:** `PUT /api/alerts/{id}`

**Path Parameters:**
- `id` (Long, required) - The alert ID

**Request Body:**
```json
{
  "title": "Malware Detection - Quarantined",
  "description": "Trojan detected and quarantined successfully",
  "severity": "CRITICAL",
  "status": "RESOLVED",
  "sourceIp": "185.220.101.50",
  "destinationIp": "10.0.0.100"
}
```

**Request:**
```bash
curl -X PUT http://localhost:8080/api/alerts/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "title": "Malware Detection - Quarantined",
    "description": "Trojan detected and quarantined successfully",
    "severity": "CRITICAL",
    "status": "RESOLVED",
    "sourceIp": "185.220.101.50",
    "destinationIp": "10.0.0.100"
  }'
```

**Response:** `200 OK`
```json
{
  "id": 3,
  "title": "Malware Detection - Quarantined",
  "description": "Trojan detected and quarantined successfully",
  "severity": "CRITICAL",
  "status": "RESOLVED",
  "sourceIp": "185.220.101.50",
  "destinationIp": "10.0.0.100",
  "createdAt": "2025-12-17T12:00:00",
  "updatedAt": "2025-12-17T12:30:00"
}
```

**Error Response:** `404 Not Found`
- Returned when alert with specified ID doesn't exist

---

### 5. Delete Alert

Delete an alert by ID.

**Endpoint:** `DELETE /api/alerts/{id}`

**Path Parameters:**
- `id` (Long, required) - The alert ID

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/alerts/3 \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

**Response:** `204 No Content`
- Empty response body on successful deletion

**Error Response:** `404 Not Found`
- Returned when alert with specified ID doesn't exist

---

### 6. Get Alerts by Severity

Retrieve all alerts with a specific severity level.

**Endpoint:** `GET /api/alerts/severity/{severity}`

**Path Parameters:**
- `severity` (String, required) - The severity level (e.g., LOW, MEDIUM, HIGH, CRITICAL)

**Request:**
```bash
curl -X GET http://localhost:8080/api/alerts/severity/HIGH \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Suspicious Login Attempt",
    "description": "Multiple failed login attempts detected",
    "severity": "HIGH",
    "status": "NEW",
    "sourceIp": "192.168.1.100",
    "destinationIp": "10.0.0.50",
    "createdAt": "2025-12-17T10:30:00",
    "updatedAt": "2025-12-17T10:30:00"
  },
  {
    "id": 4,
    "title": "Brute Force Attack",
    "description": "Brute force attack on SSH service",
    "severity": "HIGH",
    "status": "INVESTIGATING",
    "sourceIp": "198.51.100.25",
    "destinationIp": "10.0.0.52",
    "createdAt": "2025-12-17T13:00:00",
    "updatedAt": "2025-12-17T13:05:00"
  }
]
```

---

### 7. Get Alerts by Status

Retrieve all alerts with a specific status.

**Endpoint:** `GET /api/alerts/status/{status}`

**Path Parameters:**
- `status` (String, required) - The status (e.g., NEW, INVESTIGATING, RESOLVED, CLOSED)

**Request:**
```bash
curl -X GET http://localhost:8080/api/alerts/status/NEW \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Suspicious Login Attempt",
    "description": "Multiple failed login attempts detected",
    "severity": "HIGH",
    "status": "NEW",
    "sourceIp": "192.168.1.100",
    "destinationIp": "10.0.0.50",
    "createdAt": "2025-12-17T10:30:00",
    "updatedAt": "2025-12-17T10:30:00"
  },
  {
    "id": 5,
    "title": "DDoS Attempt",
    "description": "Distributed denial of service attack detected",
    "severity": "CRITICAL",
    "status": "NEW",
    "sourceIp": "Multiple",
    "destinationIp": "10.0.0.1",
    "createdAt": "2025-12-17T14:00:00",
    "updatedAt": "2025-12-17T14:00:00"
  }
]
```

---

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request succeeded with no response body |
| 400 | Bad Request - Invalid request or validation error |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error |

---

## Error Response Format

All error responses follow this format:

```json
{
  "timestamp": "2025-12-17T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/alerts"
}
```

For validation errors:
```json
{
  "timestamp": "2025-12-17T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "title",
      "message": "must not be blank"
    },
    {
      "field": "severity",
      "message": "must not be null"
    }
  ]
}
```

---

## Actuator Endpoints

Spring Boot Actuator provides monitoring and management endpoints.

### Health Check
**Endpoint:** `GET /actuator/health`

**Response:** `200 OK`
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Metrics
**Endpoint:** `GET /actuator/metrics`

**Response:** Lists available metrics

### Application Info
**Endpoint:** `GET /actuator/info`

**Response:** Application information

---

## CORS Configuration

The API currently allows CORS requests from:
- `http://localhost:3000` (configured)

For local development with Vite (port 5173), you may need to update the CORS configuration in `AlertController.java`:

```java
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
```

---

## Testing with cURL

### Quick Test Script
```bash
#!/bin/bash
BASE_URL="http://localhost:8080/api/alerts"
AUTH="admin:admin123"

# Create an alert
echo "Creating alert..."
curl -u $AUTH -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Alert",
    "description": "Testing API",
    "severity": "LOW",
    "status": "NEW"
  }'

# Get all alerts
echo -e "\n\nGetting all alerts..."
curl -u $AUTH -X GET $BASE_URL

# Get alerts by severity
echo -e "\n\nGetting HIGH severity alerts..."
curl -u $AUTH -X GET $BASE_URL/severity/HIGH
```

---

## Postman Collection

You can import these endpoints into Postman by creating a new collection with:
- Base URL: `http://localhost:8080`
- Authorization: Basic Auth (username: `admin`, password: `admin123`)
- Headers: `Content-Type: application/json`

---

## Next Steps

1. Consider implementing pagination for large datasets
2. Add filtering and sorting capabilities
3. Implement more granular authentication/authorization
4. Add rate limiting
5. Consider adding OpenAPI/Swagger documentation for interactive API docs
6. Add request/response logging
7. Implement API versioning (e.g., `/api/v1/alerts`)
