package com.example.historygo.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Rating {
    private Integer touristSpotId;
    private Integer ratingId;
    private int rating;
    private String review;
    private String timestamp;
    private String userId;
}
