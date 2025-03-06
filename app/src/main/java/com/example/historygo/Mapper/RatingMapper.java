package com.example.historygo.Mapper;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.example.historygo.dto.RatingDto;

public class RatingMapper {
    public static Document ratingToDynamoDocument(RatingDto ratingRequest) {
        Document doc = new Document();
        doc.put("touristSpotId",ratingRequest.getTouristSpotId());
        doc.put("ratingId", ratingRequest.getRatingId());
        doc.put("rating", ratingRequest.getRating());
        doc.put("review", ratingRequest.getReview());
        doc.put("timestamp", ratingRequest.getTimestamp());
        doc.put("userId", ratingRequest.getUserId());
        return doc;
    }
}