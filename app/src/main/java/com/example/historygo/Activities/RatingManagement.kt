package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.example.historygo.awsServices.DynamoDBService
import com.example.historygo.databinding.ActivityReviewManagementBinding

class RatingManagement : AppCompatActivity() {
    private lateinit var binding: ActivityReviewManagementBinding
    private lateinit var dynamoService: DynamoDBService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accessToken = intent.getStringExtra("TOKEN")
        Log.d("ReviewManagement", "JWT token: $accessToken")

        dynamoService = DynamoDBService(accessToken, baseContext)
        queryReviews();
    }

    @SuppressLint("SetTextI18n")
    fun queryReviews(){
        //I need a method to convert a Document to Object with mapper so I can display it in PlainTexts
        //var review = dynamoService.getRatingById("0");
        binding.touristSpot.setText("Test")
        //binding.Review.setText(review.get("review").toString());
    }
}