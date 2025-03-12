package com.example.historygo.Services

import android.util.Log
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.example.historygo.Mapper.RatingMapper
import com.example.historygo.awsServices.DynamoDBService
import com.example.historygo.dto.RatingDto


class RatingService(private var dynamoService: DynamoDBService) {
    private var dbTable: Table = dynamoService.setTableRating()

    fun createRating(ratingDto: RatingDto) {
        dbTable.putItem(RatingMapper.ratingToDynamoDocument(ratingDto))
    }

    fun update(ratingDto: RatingDto?) {
        try {
            val ratingDoc = RatingMapper.ratingToDynamoDocument(ratingDto)
            Log.e("test", ratingDoc.toString())
            val updatedDoc = dbTable.putItem(
                ratingDoc
            )
            Log.d("DynamoDBService", "Updated document: $updatedDoc")
        } catch (e: java.lang.Exception) {
            Log.e("DynamoDBService", "Error updating rating", e)
        }
    }

    fun delete(touristSpotId: Primitive , ratingId: Primitive ) {
        dbTable.deleteItem(
            touristSpotId,
            ratingId
        )
    }

    fun getRatingById(touristSpotId: Number, ratingId: Number): Document? {
        return try {
            dbTable.getItem(
                Primitive(touristSpotId),
                Primitive(ratingId)
            )
        } catch (e: Exception) {
            Log.e("DynamoDBService", "Error retrieving rating", e)
            null
        }
    }

    fun getAllRatings(): List<Document> {
        return try {
            val results = dbTable.scan(ScanOperationConfig())

            results.allResults.toList()
        } catch (e: Exception) {
            Log.e("DynamoDBService", "Error retrieving all ratings", e)
            emptyList()
        }
    }
}