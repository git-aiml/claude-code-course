package com.radioawa.dto;

import com.radioawa.entity.RatingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RatingRequest {
    @NotBlank(message = "Station code is required")
    private String stationCode;

    @NotBlank(message = "Artist is required")
    private String artist;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Rating type is required")
    private RatingType ratingType;

    private String ipAddress; // Optional, will be set by controller

    // Getters and Setters
    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public RatingType getRatingType() {
        return ratingType;
    }

    public void setRatingType(RatingType ratingType) {
        this.ratingType = ratingType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
