package com.radioawa.service;

import com.radioawa.dto.RatingCountsResponse;
import com.radioawa.dto.RatingRequest;
import com.radioawa.dto.RatingResponse;
import com.radioawa.entity.Rating;
import com.radioawa.entity.RatingType;
import com.radioawa.entity.Song;
import com.radioawa.repository.RatingRepository;
import com.radioawa.repository.SongRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    private final SongRepository songRepository;
    private final RatingRepository ratingRepository;

    // Rate limiting: Max votes per IP per hour
    private static final int MAX_VOTES_PER_HOUR_PER_IP = 20;
    private static final int RATE_LIMIT_HOURS = 1;

    public RatingService(SongRepository songRepository, RatingRepository ratingRepository) {
        this.songRepository = songRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public RatingResponse submitRating(RatingRequest request) {
        // IP-based rate limiting check
        if (request.getIpAddress() != null && !request.getIpAddress().isEmpty()) {
            java.time.LocalDateTime rateLimitStart = java.time.LocalDateTime.now().minusHours(RATE_LIMIT_HOURS);
            long recentVotesFromIp = ratingRepository.countByIpAddressAndCreatedAtAfter(
                request.getIpAddress(), rateLimitStart);

            if (recentVotesFromIp >= MAX_VOTES_PER_HOUR_PER_IP) {
                throw new RuntimeException("Rate limit exceeded. Maximum " + MAX_VOTES_PER_HOUR_PER_IP +
                    " votes per hour allowed.");
            }
        }
        // Find or create song
        Song song = songRepository.findByArtistAndTitle(request.getArtist(), request.getTitle())
                .orElseGet(() -> {
                    Song newSong = new Song();
                    newSong.setArtist(request.getArtist());
                    newSong.setTitle(request.getTitle());
                    newSong.setThumbsUpCount(0);
                    newSong.setThumbsDownCount(0);
                    return songRepository.save(newSong);
                });

        // Check if user already rated this song
        Optional<Rating> existingRating = ratingRepository.findBySongAndUserId(song, request.getUserId());

        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();

            // If same rating, do nothing (idempotent)
            if (rating.getRatingType() == request.getRatingType()) {
                return buildRatingResponse(song, request.getRatingType(), "Rating already submitted");
            }

            // Change vote: decrement old count, increment new count
            RatingType oldType = rating.getRatingType();
            if (oldType == RatingType.THUMBS_UP) {
                song.setThumbsUpCount(Math.max(0, song.getThumbsUpCount() - 1));
                song.setThumbsDownCount(song.getThumbsDownCount() + 1);
            } else {
                song.setThumbsDownCount(Math.max(0, song.getThumbsDownCount() - 1));
                song.setThumbsUpCount(song.getThumbsUpCount() + 1);
            }

            rating.setRatingType(request.getRatingType());
            rating.setIpAddress(request.getIpAddress()); // Update IP address
            ratingRepository.save(rating);
            songRepository.save(song);

            return buildRatingResponse(song, request.getRatingType(), "Rating updated successfully");
        }

        // New rating
        Rating newRating = new Rating();
        newRating.setSong(song);
        newRating.setUserId(request.getUserId());
        newRating.setIpAddress(request.getIpAddress());
        newRating.setRatingType(request.getRatingType());
        ratingRepository.save(newRating);

        // Increment count
        if (request.getRatingType() == RatingType.THUMBS_UP) {
            song.setThumbsUpCount(song.getThumbsUpCount() + 1);
        } else {
            song.setThumbsDownCount(song.getThumbsDownCount() + 1);
        }
        songRepository.save(song);

        return buildRatingResponse(song, request.getRatingType(), "Rating submitted successfully");
    }

    public RatingCountsResponse getRatingCounts(String artist, String title, String userId) {
        Optional<Song> songOpt = songRepository.findByArtistAndTitle(artist, title);

        if (songOpt.isEmpty()) {
            // Song not rated yet
            return new RatingCountsResponse(null, artist, title, 0, 0, null);
        }

        Song song = songOpt.get();
        RatingType userRating = null;

        if (userId != null && !userId.isEmpty()) {
            Optional<Rating> ratingOpt = ratingRepository.findBySongAndUserId(song, userId);
            userRating = ratingOpt.map(Rating::getRatingType).orElse(null);
        }

        return new RatingCountsResponse(
            song.getId(),
            song.getArtist(),
            song.getTitle(),
            song.getThumbsUpCount(),
            song.getThumbsDownCount(),
            userRating
        );
    }

    private RatingResponse buildRatingResponse(Song song, RatingType userRating, String message) {
        return new RatingResponse(
            song.getId(),
            song.getArtist(),
            song.getTitle(),
            song.getThumbsUpCount(),
            song.getThumbsDownCount(),
            userRating,
            message
        );
    }
}
