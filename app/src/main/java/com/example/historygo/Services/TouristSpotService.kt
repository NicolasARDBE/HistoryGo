package com.example.historygo.Services

import android.util.Log
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.example.historygo.AwsServices.DynamoDBService

class TouristSpotService (private var dynamoService: DynamoDBService){
    private var dbTable: Table = dynamoService.setTableTouristSpot()

    fun getTouristSpotById(touristSpotId: Number): Document? {
        return try {
            dbTable.getItem(
                Primitive(touristSpotId)
            )
        } catch (e: Exception) {
            Log.e("DynamoDBService", "Error retrieving spot", e)
            null
        }
    }

    fun getAllTouristSpot(): List<Document> {
        return try {
            val results = dbTable.scan(ScanOperationConfig())

            results.allResults.toList()
        } catch (e: Exception) {
            Log.e("DynamoDBService", "Error retrieving all spots", e)
            emptyList()
        }
    }
}