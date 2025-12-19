package com.radioawa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radioawa.dto.RatingCountsResponse;
import com.radioawa.dto.RatingRequest;
import com.radioawa.dto.RatingResponse;
import com.radioawa.entity.RatingType;
import com.radioawa.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for RatingController
 * Uses a mock RatingService implementation for testing
 */
@DisplayName("RatingController Tests")
class RatingControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new MockRatingService();
        RatingController controller = new RatingController(ratingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should accept valid rating request and return 200")
    void submitRating_validPayload_returns200() throws Exception {
        RatingRequest request = createValidRequest();

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.artist").value("Test Artist"))
            .andExpect(jsonPath("$.title").value("Test Song"))
            .andExpect(jsonPath("$.userRating").value("THUMBS_UP"));
    }


    @Test
    @DisplayName("Should accept THUMBS_DOWN rating type")
    void submitRating_thumbsDownType_success() throws Exception {
        RatingRequest request = createValidRequest();
        request.setRatingType(RatingType.THUMBS_DOWN);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userRating").value("THUMBS_DOWN"));
    }

    @Test
    @DisplayName("Should get rating counts")
    void getRatingCounts_success() throws Exception {
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Test Artist")
                .param("title", "Test Song")
                .param("userId", "user-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.thumbsUpCount").isNumber())
            .andExpect(jsonPath("$.thumbsDownCount").isNumber());
    }

    @Test
    @DisplayName("Should get rating counts without userId")
    void getRatingCounts_noUserId_success() throws Exception {
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Test Artist")
                .param("title", "Test Song"))
            .andExpect(status().isOk());
    }

    // Helper methods

    private RatingRequest createValidRequest() {
        RatingRequest request = new RatingRequest();
        request.setStationCode("ENGLISH");
        request.setArtist("Test Artist");
        request.setTitle("Test Song");
        request.setUserId("user-123");
        request.setRatingType(RatingType.THUMBS_UP);
        return request;
    }

    /**
     * Mock implementation of RatingService for testing
     */
    static class MockRatingService extends RatingService {
        public MockRatingService() {
            // Create with null repositories - won't be used in these tests
            super(null, null, null);
        }

        @Override
        public RatingResponse submitRating(RatingRequest request) {
            return new RatingResponse(
                1L,
                request.getArtist(),
                request.getTitle(),
                1,
                0,
                request.getRatingType(),
                "Rating submitted successfully"
            );
        }

        @Override
        public RatingCountsResponse getRatingCounts(String stationCode, String artist, String title, String userId) {
            return new RatingCountsResponse(
                1L,
                artist,
                title,
                10,
                2,
                userId != null ? RatingType.THUMBS_UP : null
            );
        }
    }
}
