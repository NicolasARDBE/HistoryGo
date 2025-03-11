package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.awsServices.DynamoDBService
import com.example.historygo.awsServices.DynamoDBInitializationCallback
import com.example.historygo.databinding.ActivityReviewManagementBinding
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document

class RatingManagement : AppCompatActivity(), DynamoDBInitializationCallback {
    private lateinit var binding: ActivityReviewManagementBinding
    private lateinit var dynamoService: DynamoDBService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jwtToken = intent.getStringExtra("JWTTOKEN")
        val idToken = intent.getStringExtra("IDTOKEN")

        // Callback
        dynamoService = DynamoDBService(baseContext)
        dynamoService.setCallback(this)
    }

    // El callback que se ejecutará cuando DynamoDB esté listo
    override fun onDynamoDBInitialized() {
        Log.d("Entre", "HI")
        queryReviews()
    }

    @SuppressLint("SetTextI18n")
    fun queryReviews() {
        val review: Document? = dynamoService.getRatingById(0, 0)
        if (review != null) {

            binding.touristSpot.setText(review.getValue("touristSpotId").toString())
            binding.Review.setText(review.getValue("review").toString())
            binding.Rating.setText(review.getValue("rating").toString())
        }
    }
}
