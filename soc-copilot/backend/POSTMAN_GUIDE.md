# Postman Collection Guide

## Importing the Collection

1. **Open Postman**

2. **Import the Collection**
   - Click on **Import** button (top left)
   - Click **Choose Files** or drag and drop
   - Select: `Alerts-API-Postman-Collection.json`
   - Click **Import**

3. **Collection Imported!**
   - You should see "AI SOC Copilot - Alerts API" in your Collections panel

## Collection Structure

The collection contains **20 requests** organized in 2 folders:

### 📁 Alerts API (17 requests)
- **Get All Alerts** - Retrieve all alerts
- **Get Alert by ID** - Retrieve specific alert
- **Create Alert - HIGH Severity** - Create HIGH severity alert
- **Create Alert - CRITICAL Severity** - Create CRITICAL severity alert
- **Create Alert - MEDIUM Severity** - Create MEDIUM severity alert
- **Create Alert - Minimal Required Fields** - Test minimal creation
- **Create Alert - Validation Error** - Test validation (should fail)
- **Update Alert to INVESTIGATING** - Change status to investigating
- **Update Alert to RESOLVED** - Change status to resolved
- **Get Alerts by Severity** (HIGH, CRITICAL, MEDIUM, LOW) - 4 requests
- **Get Alerts by Status** (NEW, INVESTIGATING, RESOLVED, CLOSED) - 4 requests
- **Delete Alert** - Delete an alert

### 📁 Actuator Endpoints (3 requests)
- **Health Check** - Check application health
- **Get Metrics List** - View available metrics
- **Get Application Info** - Get app information

## Pre-configured Settings

### Authentication
The collection has **Basic Auth** pre-configured:
- Username: `admin`
- Password: `admin123`

### Variables
Two collection variables are set:
- `baseUrl`: `http://localhost:8080`
- `alertId`: `1` (can be dynamically updated)

### Automated Tests
Several requests include test scripts that automatically:
- Verify response status codes
- Validate response structure
- Save created alert IDs to variables

## Using the Collection

### Before You Start

1. **Start the Backend Server**
   ```bash
   cd soc-copilot/backend
   ./mvnw spring-boot:run
   ```

2. **Verify Server is Running**
   - Run the **Health Check** request first
   - Should return `{ "status": "UP" }`

### Recommended Testing Flow

**Step 1: Create Alerts**
1. Run **Create Alert - HIGH Severity**
   - Note the `id` in response
   - This ID is automatically saved to `{{alertId}}` variable

2. Run **Create Alert - CRITICAL Severity**

3. Run **Create Alert - MEDIUM Severity**

**Step 2: Retrieve Alerts**
1. Run **Get All Alerts** - See all created alerts

2. Run **Get Alert by ID** - Get specific alert (uses `{{alertId}}`)

3. Run **Get Alerts by Severity - HIGH** - Filter by severity

4. Run **Get Alerts by Status - NEW** - Filter by status

**Step 3: Update Alerts**
1. Run **Update Alert to INVESTIGATING**
   - Changes status and description

2. Run **Get Alert by ID** again - Verify changes

3. Run **Update Alert to RESOLVED**

**Step 4: Test Validation**
1. Run **Create Alert - Validation Error**
   - Should return 400 error
   - Check the error message

**Step 5: Delete Alerts**
1. Run **Delete Alert**
   - Uses `{{alertId}}` variable
   - Returns 204 No Content

2. Run **Get Alert by ID** again
   - Should return 404 Not Found

## Customizing Requests

### Changing the Alert ID
To test with a different alert ID:
1. Go to **Variables** tab in collection
2. Change `alertId` value
3. Or edit directly in the URL: `{{baseUrl}}/api/alerts/5`

### Changing Base URL
If your server runs on a different port:
1. Go to **Variables** tab
2. Change `baseUrl` to your URL (e.g., `http://localhost:9090`)

### Creating Custom Alerts
Edit the request body in any Create/Update request:

```json
{
  "title": "Your Custom Title",
  "description": "Your custom description",
  "severity": "HIGH",
  "status": "NEW",
  "sourceIp": "192.168.1.50",
  "destinationIp": "10.0.0.100"
}
```

**Valid Severity Values:**
- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

**Valid Status Values:**
- `NEW`
- `INVESTIGATING`
- `RESOLVED`
- `CLOSED`
- `FALSE_POSITIVE`

## Running Tests

### Individual Request
1. Select a request
2. Click **Send**
3. View response in the bottom panel
4. Check **Test Results** tab for automated tests

### Running Entire Collection
1. Right-click on "AI SOC Copilot - Alerts API"
2. Select **Run collection**
3. Click **Run AI SOC Copilot - Alerts API**
4. Watch all requests execute in sequence

### Collection Runner Options
- **Iterations**: Run the collection multiple times
- **Delay**: Add delay between requests (milliseconds)
- **Data**: Use CSV/JSON file for data-driven testing
- **Save responses**: Save all responses for later review

## Viewing Test Results

After running a request:
1. Click **Test Results** tab in response panel
2. Green checkmarks (✓) = passed tests
3. Red X = failed tests
4. View test scripts in **Tests** tab of request

## Tips

### Organizing Requests
- Use folders to organize related requests
- Duplicate requests for different scenarios
- Add descriptions to document each request

### Using Environments
Create different environments for different setups:
1. Click **Environments** (left sidebar)
2. Create "Local", "Development", "Testing" environments
3. Set different `baseUrl` values
4. Switch environments easily

### Monitoring
Use Postman Monitor to:
- Run collection on schedule
- Get email alerts on failures
- Track API performance over time

### Saving Responses
- Click **Save Response** in the response panel
- Add as example to the request
- Helpful for documentation and reference

## Troubleshooting

### Connection Refused Error
- **Cause**: Backend server not running
- **Solution**: Start the backend with `./mvnw spring-boot:run`

### 401 Unauthorized Error
- **Cause**: Authentication failed
- **Solution**: Check username/password in Collection Auth settings

### 404 Not Found Error
- **Cause**: Alert with specified ID doesn't exist
- **Solution**:
  - Run "Get All Alerts" to see available IDs
  - Update `{{alertId}}` variable with a valid ID

### 400 Bad Request Error
- **Cause**: Invalid request body or missing required fields
- **Solution**: Check the request body has all required fields:
  - `title` (cannot be blank)
  - `severity`
  - `status`

### Empty Response
- **Cause**: Query returned no results
- **Solution**: Create some test data first using Create Alert requests

## Next Steps

1. Explore all requests in the collection
2. Modify request bodies to create custom alerts
3. Use the Collection Runner to run all tests
4. Create your own custom requests based on these examples
5. Set up environments for different testing scenarios

---

For full API documentation, see: `API_DOCUMENTATION.md`
