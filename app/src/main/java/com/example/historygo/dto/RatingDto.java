package com.example.historygo.dto;

import java.util.HashMap;
import java.util.Map;

public class RatingDto {
    private Integer touristSpotId;
    private Integer ratingId;
    private int rating;
    private String review;
    private String timestamp;
    private String userId;

    public RatingDto(Integer touristSpotId, Integer ratingId, int rating, String review, String timestamp, String userId) {
        this.touristSpotId = touristSpotId;
        this.ratingId = ratingId;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters
    public Integer getTouristSpotId() {
        return touristSpotId;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public int getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    // Setters
    public void setTouristSpotId(Integer touristSpotId) {
        this.touristSpotId = touristSpotId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Method to convert object to a Map
    public Map<String, Object> getM() {
        Map<String, Object> map = new HashMap<>();
        map.put("touristSpotId", touristSpotId);
        map.put("ratingId", ratingId);
        map.put("rating", rating);
        map.put("review", review);
        map.put("timestamp", timestamp);
        map.put("userId", userId);
        return map;
    }
}