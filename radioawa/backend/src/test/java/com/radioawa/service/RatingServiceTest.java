package com.radioawa.service;

import com.radioawa.dto.RatingCountsResponse;
import com.radioawa.dto.RatingRequest;
import com.radioawa.dto.RatingResponse;
import com.radioawa.entity.Rating;
import com.radioawa.entity.RatingType;
import com.radioawa.entity.Song;
import com.radioawa.entity.Station;
import com.radioawa.repository.RatingRepository;
import com.radioawa.repository.SongRepository;
import com.radioawa.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService Tests")
class RatingServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private RatingService ratingService;

    private Station testStation;
    private Song testSong;
    private RatingRequest validRequest;

    @BeforeEach
    void setUp() {
        testStation = new Station();
        testStation.setId(1L);
        testStation.setCode("ENGLISH");
        testStation.setName("RadioAwa English");

        testSong = new Song();
        testSong.setId(1L);
        testSong.setStation(testStation);
        testSong.setArtist("Test Artist");
        testSong.setTitle("Test Song");
        testSong.setThumbsUpCount(0);
        testSong.setThumbsDownCount(0);

        validRequest = new RatingRequest();
        validRequest.setStationCode("ENGLISH");
        validRequest.setArtist("Test Artist");
        validRequest.setTitle("Test Song");
        validRequest.setUserId("user-123");
        validRequest.setRatingType(RatingType.THUMBS_UP);
        validRequest.setIpAddress("192.168.1.1");
    }

    @Test
    @DisplayName("Should submit new rating successfully")
    void submitRating_newRating_success() {
        // Arrange
        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(ratingRepository.countByStationAndIpAddressAndCreatedAtAfter(any(), eq("192.168.1.1"), any())).thenReturn(0L);
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song")).thenReturn(Optional.empty());
        when(songRepository.save(any(Song.class))).thenReturn(testSong);
        when(ratingRepository.findBySongAndUserId(testSong, "user-123")).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(new Rating());

        // Act
        RatingResponse response = ratingService.submitRating(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("Rating submitted successfully");
        assertThat(response.getUserRating()).isEqualTo(RatingType.THUMBS_UP);
        verify(songRepository, times(2)).save(any(Song.class));
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should reject rate-limited requests")
    void submitRating_exceedsRateLimit_throws() {
        // Arrange
        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(ratingRepository.countByStationAndIpAddressAndCreatedAtAfter(any(), eq("192.168.1.1"), any()))
            .thenReturn(20L); // Max limit reached

        // Act & Assert
        assertThatThrownBy(() -> ratingService.submitRating(validRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Rate limit exceeded. Maximum 20 votes per hour allowed per station.");

        verify(songRepository, never()).save(any());
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should handle duplicate rating (same type) idempotently")
    void submitRating_duplicateRating_idempotent() {
        // Arrange
        Rating existingRating = new Rating();
        existingRating.setId(1L);
        existingRating.setRatingType(RatingType.THUMBS_UP);
        existingRating.setUserId("user-123");

        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(ratingRepository.countByStationAndIpAddressAndCreatedAtAfter(any(), eq("192.168.1.1"), any())).thenReturn(0L);
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song")).thenReturn(Optional.of(testSong));
        when(ratingRepository.findBySongAndUserId(testSong, "user-123")).thenReturn(Optional.of(existingRating));

        // Act
        RatingResponse response = ratingService.submitRating(validRequest);

        // Assert
        assertThat(response.getMessage()).contains("already submitted");
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should update rating when user changes vote")
    void submitRating_changeRatingType_updates() {
        // Arrange
        Rating existingRating = new Rating();
        existingRating.setId(1L);
        existingRating.setRatingType(RatingType.THUMBS_DOWN);
        existingRating.setUserId("user-123");

        testSong.setThumbsDownCount(5);
        testSong.setThumbsUpCount(10);

        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(ratingRepository.countByStationAndIpAddressAndCreatedAtAfter(any(), eq("192.168.1.1"), any())).thenReturn(0L);
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song")).thenReturn(Optional.of(testSong));
        when(ratingRepository.findBySongAndUserId(testSong, "user-123")).thenReturn(Optional.of(existingRating));
        when(songRepository.save(any(Song.class))).thenReturn(testSong);
        when(ratingRepository.save(any(Rating.class))).thenReturn(existingRating);

        // Act
        RatingResponse response = ratingService.submitRating(validRequest);

        // Assert
        assertThat(response.getMessage()).contains("updated");
        assertThat(testSong.getThumbsUpCount()).isEqualTo(11);
        assertThat(testSong.getThumbsDownCount()).isEqualTo(4);
        verify(ratingRepository).save(existingRating);
        verify(songRepository).save(testSong);
    }

    @Test
    @DisplayName("Should throw exception when station not found")
    void submitRating_stationNotFound_throws() {
        // Arrange
        when(stationRepository.findByCode("INVALID")).thenReturn(Optional.empty());
        validRequest.setStationCode("INVALID");

        // Act & Assert
        assertThatThrownBy(() -> ratingService.submitRating(validRequest))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Station not found: INVALID");
    }

    @Test
    @DisplayName("Should create new song if not exists")
    void submitRating_newSong_created() {
        // Arrange
        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(ratingRepository.countByStationAndIpAddressAndCreatedAtAfter(any(), eq("192.168.1.1"), any())).thenReturn(0L);
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song")).thenReturn(Optional.empty());
        when(songRepository.save(any(Song.class))).thenReturn(testSong);
        when(ratingRepository.findBySongAndUserId(testSong, "user-123")).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(new Rating());

        // Act
        ratingService.submitRating(validRequest);

        // Assert
        verify(songRepository).save(argThat(song ->
            song.getArtist().equals("Test Artist") &&
            song.getTitle().equals("Test Song") &&
            song.getThumbsUpCount() == 0 &&
            song.getThumbsDownCount() == 0
        ));
    }

    @Test
    @DisplayName("Should retrieve rating counts for existing song")
    void getRatingCounts_existingSong_returnsCorrectCounts() {
        // Arrange
        testSong.setThumbsUpCount(42);
        testSong.setThumbsDownCount(5);

        Rating userRating = new Rating();
        userRating.setRatingType(RatingType.THUMBS_UP);

        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song"))
            .thenReturn(Optional.of(testSong));
        when(ratingRepository.findBySongAndUserId(testSong, "user-123")).thenReturn(Optional.of(userRating));

        // Act
        RatingCountsResponse response = ratingService.getRatingCounts("ENGLISH", "Test Artist", "Test Song", "user-123");

        // Assert
        assertThat(response.getThumbsUpCount()).isEqualTo(42);
        assertThat(response.getThumbsDownCount()).isEqualTo(5);
        assertThat(response.getUserRating()).isEqualTo(RatingType.THUMBS_UP);
    }

    @Test
    @DisplayName("Should return zero counts for non-existent song")
    void getRatingCounts_songNotFound_returnsZeroCounts() {
        // Arrange
        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Unknown Artist", "Unknown Song"))
            .thenReturn(Optional.empty());

        // Act
        RatingCountsResponse response = ratingService.getRatingCounts("ENGLISH", "Unknown Artist", "Unknown Song", "user-123");

        // Assert
        assertThat(response.getThumbsUpCount()).isZero();
        assertThat(response.getThumbsDownCount()).isZero();
        assertThat(response.getUserRating()).isNull();
    }

    @Test
    @DisplayName("Should not include user rating if userId not provided")
    void getRatingCounts_noUserId_noUserRating() {
        // Arrange
        testSong.setThumbsUpCount(10);
        testSong.setThumbsDownCount(2);

        when(stationRepository.findByCode("ENGLISH")).thenReturn(Optional.of(testStation));
        when(songRepository.findByStationAndArtistAndTitle(testStation, "Test Artist", "Test Song"))
            .thenReturn(Optional.of(testSong));

        // Act
        RatingCountsResponse response = ratingService.getRatingCounts("ENGLISH", "Test Artist", "Test Song", null);

        // Assert
        assertThat(response.getUserRating()).isNull();
        verify(ratingRepository, never()).findBySongAndUserId(any(), any());
    }
}
