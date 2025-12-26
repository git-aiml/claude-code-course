# radioawa - Testing Framework & Strategy

**Version:** 1.0
**Last Updated:** December 2024
**Document Type:** Testing Guide & Best Practices

---

## Table of Contents

1. [Overview](#overview)
2. [Testing Philosophy](#testing-philosophy)
3. [Backend Testing Strategy](#backend-testing-strategy)
4. [Frontend Testing Strategy](#frontend-testing-strategy)
5. [Testing Pyramid](#testing-pyramid)
6. [Running Tests](#running-tests)
7. [Code Coverage](#code-coverage)
8. [CI/CD Integration](#cicd-integration)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

---

## Overview

radioawa implements a comprehensive testing framework covering both backend and frontend components using industry-standard tools and practices.

### Testing Stack

#### Backend (Java/Spring Boot)
- **Test Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **Integration Testing**: Spring Boot Test + TestContainers
- **Assertion Library**: AssertJ
- **Build Tool**: Maven with Surefire plugin

#### Frontend (React/JavaScript)
- **Test Framework**: Vitest
- **Component Testing**: React Testing Library
- **Assertions**: Jest Matchers (included with Vitest)
- **Build Tool**: npm/Vite

### Goals

1. **High Code Coverage** - Target 80%+ code coverage
2. **Fast Feedback Loop** - Tests complete in < 5 seconds
3. **Reliable Tests** - No flaky tests; consistent results
4. **Clear Test Cases** - Descriptive names and documentation
5. **Maintainable Tests** - DRY principles, easy to update
6. **Automation** - CI/CD integration for every commit

---

## Testing Philosophy

### Key Principles

1. **Test-Driven Development (TDD)**
   - Write tests first, then implementation
   - Red → Green → Refactor cycle
   - Benefits: Better design, fewer bugs

2. **Behavior-Driven Testing**
   - Tests describe expected behavior
   - Use readable assertion messages
   - Example: "Should reject rate-limited requests"

3. **Unit Tests First**
   - Test individual components in isolation
   - Fast feedback on code changes
   - 70-75% of test suite

4. **Integration Tests**
   - Test component interactions
   - Validate database transactions
   - 20-25% of test suite

5. **E2E Tests**
   - Test complete user workflows
   - Real browser simulation
   - 5% of test suite (if included)

### Test Naming Convention

#### Backend (Java)
```
[ClassUnderTest]Test.java
[methodName]_[scenario]_[expectedResult]

Example:
RatingService.submitRating() → RatingServiceTest.java
Test method: submitRating_duplicateVote_returnsExistingRating()
```

#### Frontend (JavaScript)
```
[Component].test.jsx
[componentFunction] [scenario] [expected behavior]

Example:
SongRating.jsx → SongRating.test.jsx
Test: "Should highlight user's rating when provided"
```

---

## Backend Testing Strategy

### Testing Pyramid

```
       E2E Tests (Full Stack)
          /\
         /  \
        /    \          5%
       /______\
      Integration
       /\
      /  \
     /    \           20%
    /______\
       Unit
        /\
       /  \
      /    \          75%
     /______\
```

### Unit Tests

**Scope**: Individual class methods in isolation
**Mocking**: Dependencies are mocked
**Database**: No actual database calls
**Execution Time**: < 1 second per test

#### Classes to Test

##### 1. RatingService (Business Logic)

**File**: `backend/src/test/java/com/radioawa/service/RatingServiceTest.java`

**Test Cases**:
- ✅ Submit new rating successfully
- ✅ Update existing rating (different type)
- ✅ Prevent duplicate rating (same user, same song)
- ✅ Handle rate limiting (> 20 votes/hour)
- ✅ Create song if not exists
- ✅ Update song vote counts correctly
- ✅ Extract IP address from request headers
- ✅ Validate required fields
- ✅ Transactional behavior (rollback on error)

**Example Structure**:
```java
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("Should submit new rating successfully")
    void submitNewRating_success() {
        // Arrange
        RatingRequest request = new RatingRequest("Artist", "Title", "uuid", "THUMBS_UP", "ENGLISH");
        Song song = new Song();
        song.setId(1L);

        when(songRepository.findByArtistAndTitleAndStationCode(
            "Artist", "Title", "ENGLISH"
        )).thenReturn(Optional.empty());

        when(ratingRepository.findByUserIdAndSongIdAndStationCode(
            "uuid", 1L, "ENGLISH"
        )).thenReturn(Optional.empty());

        // Act
        RatingResponse response = ratingService.submitRating(request, "192.168.1.1");

        // Assert
        assertThat(response.getThumbsUpCount()).isEqualTo(1);
        verify(songRepository).save(any(Song.class));
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should reject requests exceeding rate limit")
    void submitRating_exceedsRateLimit_throws() {
        // Arrange
        RatingRequest request = new RatingRequest("Artist", "Title", "uuid", "THUMBS_UP", "ENGLISH");

        // Mock 20 existing ratings in last hour
        when(ratingRepository.countRecentRatings("192.168.1.1", "ENGLISH", any(LocalDateTime.class)))
            .thenReturn(20L);

        // Act & Assert
        assertThatThrownBy(() -> ratingService.submitRating(request, "192.168.1.1"))
            .isInstanceOf(RateLimitExceededException.class)
            .hasMessage("Rate limit exceeded");
    }
}
```

##### 2. Controllers (API Layer)

**File**: `backend/src/test/java/com/radioawa/controller/RatingControllerTest.java`

**Test Cases**:
- ✅ POST /api/ratings with valid payload
- ✅ GET /api/ratings/counts returns correct data
- ✅ Invalid rating type rejected (400)
- ✅ Missing required fields rejected (400)
- ✅ Rate limit error returns 429
- ✅ Server errors return 500
- ✅ CORS headers present
- ✅ Response body matches schema

**Example Structure**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Test
    @DisplayName("Should accept valid rating request")
    void submitRating_validPayload_returns200() throws Exception {
        // Arrange
        RatingRequest request = new RatingRequest(
            "Arijit Singh", "Tum Hi Ho", "uuid-123", "THUMBS_UP", "HINDI"
        );
        RatingResponse response = new RatingResponse(
            1L, "Arijit Singh", "Tum Hi Ho", 42, 5, "THUMBS_UP", "Success"
        );

        when(ratingService.submitRating(eq(request), anyString()))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.songId").value(1L))
            .andExpect(jsonPath("$.thumbsUpCount").value(42));
    }

    @Test
    @DisplayName("Should reject missing required fields")
    void submitRating_missingFields_returns400() throws Exception {
        // Arrange
        String invalidJson = "{\"artist\": \"Test\"}"; // Missing other fields

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
```

##### 3. Repositories (Data Access)

**File**: `backend/src/test/java/com/radioawa/repository/RatingRepositoryTest.java`

**Test Cases**:
- ✅ Find rating by userId and songId
- ✅ Count ratings by IP and time window
- ✅ Save and retrieve rating
- ✅ Update rating
- ✅ Delete rating
- ✅ Query with custom methods
- ✅ Transaction behavior

**Note**: Use `@DataJpaTest` for repository tests with in-memory H2 database

**Example**:
```java
@DataJpaTest
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private SongRepository songRepository;

    @Test
    @DisplayName("Should find rating by userId and songId")
    void findByUserIdAndSongId_exists_returnsRating() {
        // Arrange
        Song song = new Song();
        song.setArtist("Artist");
        song.setTitle("Title");
        song.setStationCode("ENGLISH");
        songRepository.save(song);

        Rating rating = new Rating();
        rating.setSong(song);
        rating.setUserId("user-123");
        rating.setStationCode("ENGLISH");
        ratingRepository.save(rating);

        // Act
        Optional<Rating> found = ratingRepository.findByUserIdAndSongId("user-123", song.getId());

        // Assert
        assertThat(found).isPresent()
            .hasValueSatisfying(r -> assertThat(r.getUserId()).isEqualTo("user-123"));
    }
}
```

### Integration Tests

**Scope**: Multiple components working together
**Database**: Real database or TestContainers
**Transactions**: Test rollback/commit behavior
**Execution Time**: 1-5 seconds per test

#### Integration Test Example

**File**: `backend/src/test/java/com/radioawa/integration/RatingIntegrationTest.java`

```java
@SpringBootTest
@ActiveProfiles("test")
class RatingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        songRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete rating flow: submit and retrieve counts")
    void ratingFlow_submitAndRetrieve_success() throws Exception {
        // Step 1: Submit rating
        MvcResult result = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "artist": "Test Artist",
                        "title": "Test Song",
                        "userId": "user-123",
                        "ratingType": "THUMBS_UP",
                        "stationCode": "ENGLISH"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

        // Step 2: Retrieve counts
        mockMvc.perform(get("/api/ratings/counts")
                .param("artist", "Test Artist")
                .param("title", "Test Song")
                .param("stationCode", "ENGLISH")
                .param("userId", "user-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.thumbsUpCount").value(1))
            .andExpect(jsonPath("$.userRating").value("THUMBS_UP"));
    }
}
```

### Test Data Management

#### Using TestContainers for Database

```java
@SpringBootTest
class RatingRepositoryTestContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("radioawa_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Tests here run against real PostgreSQL
}
```

---

## Frontend Testing Strategy

### Component Testing with React Testing Library

**Philosophy**: Test components as users interact with them, not implementation details

#### Key Rules
- ✅ DO: Query by role, label, text (how users see it)
- ✅ DO: Test user interactions (clicks, typing)
- ✅ DO: Mock API calls, not component internals
- ❌ DON'T: Test state directly
- ❌ DON'T: Test props directly
- ❌ DON'T: Test component structure/DOM

### Unit Tests

**Scope**: Individual component behavior
**Mocking**: API calls are mocked
**Rendering**: Real DOM via jsdom
**Execution Time**: < 500ms per test

#### Components to Test

##### 1. SongRating Component

**File**: `frontend/src/components/SongRating.test.jsx`

**Test Cases**:
- ✅ Display thumbs up/down buttons
- ✅ Submit rating on button click
- ✅ Highlight user's previous rating
- ✅ Show vote counts
- ✅ Disable buttons during submission
- ✅ Show error message on failure
- ✅ Update counts after successful submission

**Example Structure**:
```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import SongRating from './SongRating';
import * as ratingService from '../services/ratingService';

vi.mock('../services/ratingService');

describe('SongRating', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test('should display thumbs up and down buttons', () => {
        render(
            <SongRating
                artist="Test Artist"
                title="Test Song"
                userId="user-123"
                stationCode="ENGLISH"
            />
        );

        expect(screen.getByRole('button', { name: /thumbs up/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /thumbs down/i })).toBeInTheDocument();
    });

    test('should submit rating on thumbs up click', async () => {
        const user = userEvent.setup();

        ratingService.submitRating.mockResolvedValue({
            thumbsUpCount: 1,
            thumbsDownCount: 0,
            userRating: 'THUMBS_UP'
        });

        render(
            <SongRating
                artist="Test Artist"
                title="Test Song"
                userId="user-123"
                stationCode="ENGLISH"
            />
        );

        const thumbsUpButton = screen.getByRole('button', { name: /thumbs up/i });
        await user.click(thumbsUpButton);

        await waitFor(() => {
            expect(ratingService.submitRating).toHaveBeenCalledWith(
                'Test Artist',
                'Test Song',
                'user-123',
                'THUMBS_UP',
                'ENGLISH'
            );
        });
    });

    test('should disable buttons during submission', async () => {
        const user = userEvent.setup();

        ratingService.submitRating.mockImplementation(
            () => new Promise(resolve => setTimeout(resolve, 100))
        );

        render(
            <SongRating
                artist="Test Artist"
                title="Test Song"
                userId="user-123"
                stationCode="ENGLISH"
            />
        );

        const thumbsUpButton = screen.getByRole('button', { name: /thumbs up/i });
        await user.click(thumbsUpButton);

        expect(thumbsUpButton).toBeDisabled();

        await waitFor(() => {
            expect(thumbsUpButton).not.toBeDisabled();
        });
    });

    test('should highlight user rating', () => {
        render(
            <SongRating
                artist="Test Artist"
                title="Test Song"
                userId="user-123"
                userRating="THUMBS_UP"
                stationCode="ENGLISH"
            />
        );

        const thumbsUpButton = screen.getByRole('button', { name: /thumbs up/i });
        expect(thumbsUpButton).toHaveClass('highlighted');
    });

    test('should show error message on submission failure', async () => {
        const user = userEvent.setup();

        ratingService.submitRating.mockRejectedValue(
            new Error('Rate limit exceeded')
        );

        render(
            <SongRating
                artist="Test Artist"
                title="Test Song"
                userId="user-123"
                stationCode="ENGLISH"
            />
        );

        const thumbsUpButton = screen.getByRole('button', { name: /thumbs up/i });
        await user.click(thumbsUpButton);

        await waitFor(() => {
            expect(screen.getByText(/rate limit exceeded/i)).toBeInTheDocument();
        });
    });
});
```

##### 2. RadioPlayer Component

**File**: `frontend/src/components/RadioPlayer.test.jsx`

**Test Cases**:
- ✅ Render play/pause button
- ✅ Play stream on button click
- ✅ Pause stream
- ✅ Update status from "Ready" to "LIVE"
- ✅ Show volume control
- ✅ Handle stream errors

##### 3. StationSelector Component

**File**: `frontend/src/components/StationSelector.test.jsx`

**Test Cases**:
- ✅ Display list of stations
- ✅ Highlight current station
- ✅ Switch station on click
- ✅ Save selection to localStorage
- ✅ Load last selected station

### Service Tests

**File**: `frontend/src/services/ratingService.test.js`

**Test Cases**:
- ✅ submitRating makes POST request to /api/ratings
- ✅ getRatingCounts makes GET request
- ✅ Handle network errors
- ✅ Parse response correctly
- ✅ Timeout handling

**Example**:
```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest';
import * as ratingService from './ratingService';

global.fetch = vi.fn();

describe('ratingService', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test('submitRating should POST to /api/ratings', async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            json: () => Promise.resolve({
                thumbsUpCount: 1,
                thumbsDownCount: 0
            })
        });

        const result = await ratingService.submitRating(
            'Artist',
            'Title',
            'user-123',
            'THUMBS_UP',
            'ENGLISH'
        );

        expect(global.fetch).toHaveBeenCalledWith(
            expect.stringContaining('/api/ratings'),
            expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    'Content-Type': 'application/json'
                })
            })
        );

        expect(result.thumbsUpCount).toBe(1);
    });

    test('should handle network errors', async () => {
        global.fetch.mockRejectedValue(new Error('Network error'));

        await expect(
            ratingService.submitRating('Artist', 'Title', 'user-123', 'THUMBS_UP', 'ENGLISH')
        ).rejects.toThrow('Network error');
    });
});
```

### Integration Tests

**File**: `frontend/src/integration/radioPlayer.integration.test.jsx`

**Test Cases**:
- ✅ User can select station
- ✅ User can rate song
- ✅ Rating reflects in UI
- ✅ Station persists on reload
- ✅ Error handling flows

---

## Testing Pyramid

### Distribution

```
Total Test Suite: 100%

Unit Tests: 75%
├── Backend: 40% (services, utilities)
├── Frontend: 35% (components, services)

Integration Tests: 20%
├── Backend: 10% (API, database)
├── Frontend: 10% (component interactions)

E2E Tests: 5%
└── End-to-end user flows
```

### Execution Time Targets

| Test Type | Count | Avg Time | Total Time |
|-----------|-------|----------|-----------|
| Unit | 45 | 50ms | 2.25s |
| Integration | 12 | 300ms | 3.6s |
| E2E | 2 | 2000ms | 4s |
| **Total** | **59** | **~100ms** | **~10s** |

---

## Running Tests

### Using Make Targets (Docker - Recommended) ⭐

If you're using Docker, the easiest way to run tests is with Make targets:

```bash
# Run all tests (backend + frontend)
make test

# Run backend tests only
make test-backend

# Run frontend tests only
make test-frontend

# Run API integration tests (requires dev environment running)
make test-api
```

**Benefits:**
- ✅ Works regardless of local environment
- ✅ Tests run in same environment as production
- ✅ No need to install Java, Node.js, or dependencies locally
- ✅ Simple, memorable commands

**See [README.md](./README.md#why-use-make-targets-recommended-approach) for more on why Make is recommended.**

---

### Manual Testing (Without Docker)

#### Backend

```bash
# Run all tests
cd backend
mvn test

# Run specific test class
mvn test -Dtest=RatingServiceTest

# Run specific test method
mvn test -Dtest=RatingServiceTest#submitNewRating_success

# Run with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

#### Frontend

```bash
# Run all tests
cd frontend
npm run test

# Watch mode (re-run on file change)
npm run test:watch

# Run with coverage
npm run test:coverage

# Run specific test file
npm run test -- SongRating.test.jsx

# View coverage report
open coverage/index.html
```

### Both

```bash
# From root directory (if scripts available)
./test-all.sh
```

---

## Code Coverage

### Targets

- **Overall**: 80%+
- **Statements**: 80%+
- **Branches**: 75%+
- **Functions**: 80%+
- **Lines**: 80%+

### Backend Coverage (JaCoCo)

```bash
mvn clean test jacoco:report
```

Report location: `backend/target/site/jacoco/index.html`

**Coverage Metrics**:
- Controllers: 90%+ (all endpoints tested)
- Services: 85%+ (all business logic)
- Repositories: 70%+ (basic CRUD operations)
- Entities: 100% (data models don't need tests)

### Frontend Coverage (Vitest)

```bash
npm run test:coverage
```

Report location: `frontend/coverage/index.html`

**Coverage Metrics**:
- Components: 85%+ (all user interactions)
- Services: 90%+ (API calls)
- Utils: 100% (pure functions)
- Hooks: 80%+ (custom hooks)

---

## CI/CD Integration

### GitHub Actions Workflow

**File**: `.github/workflows/test.yml`

```yaml
name: Tests

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: radioawa_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3

      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run backend tests
        run: |
          cd backend
          mvn clean test

      - name: Generate coverage report
        run: |
          cd backend
          mvn jacoco:report

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: ./backend/target/site/jacoco/jacoco.xml
          flags: backend

  frontend-tests:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json

      - name: Install dependencies
        run: |
          cd frontend
          npm install

      - name: Run frontend tests
        run: |
          cd frontend
          npm run test

      - name: Generate coverage report
        run: |
          cd frontend
          npm run test:coverage

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./frontend/coverage/coverage-final.json
          flags: frontend

  quality-gates:
    runs-on: ubuntu-latest
    needs: [backend-tests, frontend-tests]
    if: always()

    steps:
      - name: Check test status
        run: |
          if [[ ${{ needs.backend-tests.result }} != 'success' ]] || \
             [[ ${{ needs.frontend-tests.result }} != 'success' ]]; then
            echo "Tests failed!"
            exit 1
          fi

      - name: Summary
        run: echo "All tests passed! ✅"
```

---

## Best Practices

### General Guidelines

1. **Test Behavior, Not Implementation**
   - ❌ Bad: `expect(component.state.isLoading).toBe(false)`
   - ✅ Good: `expect(screen.queryByText('Loading...')).not.toBeInTheDocument()`

2. **Use Descriptive Test Names**
   - ❌ Bad: `test('works')`
   - ✅ Good: `test('should highlight user rating when userRating prop is provided')`

3. **Follow AAA Pattern (Arrange, Act, Assert)**
   ```java
   @Test
   void example() {
       // Arrange - setup test data
       User user = new User("John");

       // Act - perform action
       boolean result = userService.isAdult(user);

       // Assert - verify outcome
       assertThat(result).isTrue();
   }
   ```

4. **One Assertion Per Test (When Possible)**
   - Each test should verify one behavior
   - Multiple related assertions are OK for testing side effects

5. **Keep Tests Independent**
   - No test should depend on another test
   - Use setUp/beforeEach for common initialization
   - Clean up after tests with tearDown/afterEach

6. **Mock External Dependencies**
   - Database calls → Mock repositories
   - API calls → Mock services
   - File system → Use temporary files or mocks

7. **Test Edge Cases**
   - Empty input
   - Null values
   - Maximum/minimum values
   - Boundary conditions

8. **Use Fixtures for Test Data**
   ```java
   // Bad
   @Test
   void test1() { new Song("a", "b", "c"); }
   @Test
   void test2() { new Song("a", "b", "c"); }

   // Good
   @BeforeEach
   void setUp() {
       song = new Song("a", "b", "c");
   }
   ```

### Backend Best Practices

1. **Use Spring Boot Test Slices**
   ```java
   @WebMvcTest(RatingController.class)  // Only load web layer
   @DataJpaTest                         // Only load persistence layer
   @SpringBootTest                      // Load entire application
   ```

2. **Mock at Component Boundaries**
   - Mock repositories in service tests
   - Mock services in controller tests
   - Use real database in integration tests

3. **Test Transactional Behavior**
   ```java
   @Transactional
   @Test
   void testRollback() {
       // Automatic rollback after test
   }
   ```

4. **Use AssertJ for Fluent Assertions**
   ```java
   // Fluent and readable
   assertThat(ratings)
       .hasSize(2)
       .extracting(Rating::getType)
       .containsExactly(RatingType.THUMBS_UP, RatingType.THUMBS_DOWN);
   ```

### Frontend Best Practices

1. **Query by Accessibility**
   ```javascript
   // Good - query as user sees it
   screen.getByRole('button', { name: /submit/i })
   screen.getByLabelText('Email')
   screen.getByText('Song Title')

   // Bad - implementation details
   container.querySelector('.rating-button')
   ```

2. **Use userEvent for Interactions**
   ```javascript
   // Prefer userEvent (simulates real user)
   await user.click(button)
   await user.type(input, 'text')

   // Over fireEvent (synthetic events)
   fireEvent.click(button)
   ```

3. **Test Component Integration**
   ```javascript
   // Test interaction between multiple components
   render(
       <RatingProvider>
           <SongRating />
           <RatingCounter />
       </RatingProvider>
   )
   ```

4. **Mock API Calls Consistently**
   ```javascript
   // Setup mock before component renders
   vi.mock('./services/ratingService', () => ({
       submitRating: vi.fn()
   }))
   ```

---

## Troubleshooting

### Backend Issues

#### Tests hang or timeout
- Check for infinite loops or network calls
- Increase timeout: `@Test(timeout = 5000)`
- Check mocked method calls: `verify(mock, never()).method()`

#### Database tests fail
- Ensure PostgreSQL is running for integration tests
- Use H2 in-memory database for unit tests
- Check transaction rollback: `@Transactional`

#### Import errors
- Maven: Run `mvn clean install`
- Check Spring Boot version compatibility

### Frontend Issues

#### Tests hang
- Check for unmocked async operations
- Verify all promises are awaited
- Use `vi.fake Timers()` for timer tests

#### Component not rendering
- Check mocks are set up before render
- Verify props are passed correctly
- Use `screen.debug()` to see DOM

#### API call tests fail
- Ensure fetch is mocked globally
- Check request parameters match
- Verify response structure

### CI/CD Issues

#### GitHub Actions fail locally
- Use `act` to run workflows locally
- Check Node/Java versions match
- Ensure all environment variables set

---

## Test Examples Repository

Complete examples available in:
- **Backend**: `backend/src/test/java/com/radioawa/`
- **Frontend**: `frontend/src/__tests__/`

---

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [React Testing Library](https://testing-library.com/react)
- [Vitest Documentation](https://vitest.dev/)
- [TestContainers](https://testcontainers.com/)

---

**Document Version**: 1.0
**Last Updated**: December 2024
**Author**: Sujit K Singh
**Status**: Living Document
