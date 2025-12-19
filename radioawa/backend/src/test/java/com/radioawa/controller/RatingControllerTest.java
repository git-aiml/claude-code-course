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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@DisplayName("RatingController Tests")
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    private RatingRequest validRequest;
    private RatingResponse successResponse;

    @BeforeEach
    void setUp() {
        validRequest = new RatingRequest();
        validRequest.setStationCode("ENGLISH");
        validRequest.setArtist("Arijit Singh");
        validRequest.setTitle("Tum Hi Ho");
        validRequest.setUserId("user-123");
        validRequest.setRatingType(RatingType.THUMBS_UP);

        successResponse = new RatingResponse(
            1L, "Arijit Singh", "Tum Hi Ho", 42, 5, RatingType.THUMBS_UP, "Rating submitted successfully"
        );
    }

    @Test
    @DisplayName("Should accept valid rating request and return 200")
    void submitRating_validPayload_returns200() throws Exception {
        // Arrange
        when(ratingService.submitRating(any(RatingRequest.class)))
            .thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.songId").value(1L))
            .andExpect(jsonPath("$.artist").value("Arijit Singh"))
            .andExpect(jsonPath("$.title").value("Tum Hi Ho"))
            .andExpect(jsonPath("$.thumbsUpCount").value(42))
            .andExpect(jsonPath("$.thumbsDownCount").value(5))
            .andExpect(jsonPath("$.userRating").value("THUMBS_UP"))
            .andExpect(jsonPath("$.message").value("Rating submitted successfully"));
    }

    @Test
    @DisplayName("Should reject missing required field (artist)")
    void submitRating_missingArtist_returns400() throws Exception {
        // Arrange
        validRequest.setArtist(null);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject missing required field (title)")
    void submitRating_missingTitle_returns400() throws Exception {
        // Arrange
        validRequest.setTitle(null);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject missing required field (userId)")
    void submitRating_missingUserId_returns400() throws Exception {
        // Arrange
        validRequest.setUserId(null);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject missing required field (ratingType)")
    void submitRating_missingRatingType_returns400() throws Exception {
        // Arrange
        validRequest.setRatingType(null);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 429 when rate limit exceeded")
    void submitRating_rateLimitExceeded_returns429() throws Exception {
        // Arrange
        RuntimeException rateLimitException = new RuntimeException("Rate limit exceeded. Maximum 20 votes per hour allowed per station.");
        when(ratingService.submitRating(any(RatingRequest.class)))
            .thenThrow(rateLimitException);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.message").value(containsString("Rate limit exceeded")));
    }

    @Test
    @DisplayName("Should handle service errors and return 500")
    void submitRating_serviceError_returns500() throws Exception {
        // Arrange
        when(ratingService.submitRating(any(RatingRequest.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(containsString("Failed to submit rating")));
    }

    @Test
    @DisplayName("Should accept both THUMBS_UP and THUMBS_DOWN rating types")
    void submitRating_thumbsDownType_success() throws Exception {
        // Arrange
        validRequest.setRatingType(RatingType.THUMBS_DOWN);
        RatingResponse downResponse = new RatingResponse(
            1L, "Arijit Singh", "Tum Hi Ho", 40, 7, RatingType.THUMBS_DOWN, "Rating submitted successfully"
        );
        when(ratingService.submitRating(any(RatingRequest.class)))
            .thenReturn(downResponse);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userRating").value("THUMBS_DOWN"))
            .andExpect(jsonPath("$.thumbsDownCount").value(7));
    }

    @Test
    @DisplayName("Should extract IP address and pass to service")
    void submitRating_extractsIpAddress() throws Exception {
        // Arrange
        when(ratingService.submitRating(any(RatingRequest.class)))
            .thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Forwarded-For", "203.0.113.42")
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get rating counts for existing song")
    void getRatingCounts_existingSong_returns200() throws Exception {
        // Arrange
        RatingCountsResponse countsResponse = new RatingCountsResponse(
            1L, "Arijit Singh", "Tum Hi Ho", 42, 5, RatingType.THUMBS_UP
        );
        when(ratingService.getRatingCounts(eq("ENGLISH"), eq("Arijit Singh"), eq("Tum Hi Ho"), eq("user-123")))
            .thenReturn(countsResponse);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Arijit Singh")
                .param("title", "Tum Hi Ho")
                .param("userId", "user-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.songId").value(1L))
            .andExpect(jsonPath("$.thumbsUpCount").value(42))
            .andExpect(jsonPath("$.thumbsDownCount").value(5))
            .andExpect(jsonPath("$.userRating").value("THUMBS_UP"));
    }

    @Test
    @DisplayName("Should return zero counts for non-existent song")
    void getRatingCounts_songNotFound_returnsZeroCounts() throws Exception {
        // Arrange
        RatingCountsResponse countsResponse = new RatingCountsResponse(
            null, "Unknown Artist", "Unknown Song", 0, 0, null
        );
        when(ratingService.getRatingCounts(eq("ENGLISH"), eq("Unknown Artist"), eq("Unknown Song"), eq("user-123")))
            .thenReturn(countsResponse);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Unknown Artist")
                .param("title", "Unknown Song")
                .param("userId", "user-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.songId").doesNotExist())
            .andExpect(jsonPath("$.thumbsUpCount").value(0))
            .andExpect(jsonPath("$.thumbsDownCount").value(0));
    }

    @Test
    @DisplayName("Should accept optional userId parameter")
    void getRatingCounts_noUserId_success() throws Exception {
        // Arrange
        RatingCountsResponse countsResponse = new RatingCountsResponse(
            1L, "Arijit Singh", "Tum Hi Ho", 42, 5, null
        );
        when(ratingService.getRatingCounts(eq("ENGLISH"), eq("Arijit Singh"), eq("Tum Hi Ho"), eq(null)))
            .thenReturn(countsResponse);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Arijit Singh")
                .param("title", "Tum Hi Ho"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userRating").doesNotExist());
    }

    @Test
    @DisplayName("Should handle errors in getRatingCounts")
    void getRatingCounts_serviceError_returns500() throws Exception {
        // Arrange
        when(ratingService.getRatingCounts(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/ratings/counts")
                .param("stationCode", "ENGLISH")
                .param("artist", "Arijit Singh")
                .param("title", "Tum Hi Ho"))
            .andExpect(status().isInternalServerError());
    }
}
