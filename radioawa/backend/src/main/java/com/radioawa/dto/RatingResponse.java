package com.radioawa.dto;

import com.radioawa.entity.RatingType;

public class RatingResponse {
    private Long songId;
    private String artist;
    private String title;
    private Integer thumbsUpCount;
    private Integer thumbsDownCount;
    private RatingType userRating; // null if user hasn't rated
    private String message;

    public RatingResponse() {
    }

    public RatingResponse(Long songId, String artist, String title, Integer thumbsUpCount,
                         Integer thumbsDownCount, RatingType userRating, String message) {
        this.songId = songId;
        this.artist = artist;
        this.title = title;
        this.thumbsUpCount = thumbsUpCount;
        this.thumbsDownCount = thumbsDownCount;
        this.userRating = userRating;
        this.message = message;
    }

    // Getters and Setters
    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
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

    public Integer getThumbsUpCount() {
        return thumbsUpCount;
    }

    public void setThumbsUpCount(Integer thumbsUpCount) {
        this.thumbsUpCount = thumbsUpCount;
    }

    public Integer getThumbsDownCount() {
        return thumbsDownCount;
    }

    public void setThumbsDownCount(Integer thumbsDownCount) {
        this.thumbsDownCount = thumbsDownCount;
    }

    public RatingType getUserRating() {
        return userRating;
    }

    public void setUserRating(RatingType userRating) {
        this.userRating = userRating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
