package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.awsServices.DynamoDBService
import com.example.historygo.awsServices.DynamoDBInitializationCallback
import com.example.historygo.databinding.ActivityReviewManagementBinding
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive
import com.example.historygo.Services.RatingService
import com.example.historygo.Services.TouristSpotService
import com.example.historygo.dto.RatingDto

class RatingManagement : AppCompatActivity(), DynamoDBInitializationCallback {
    private lateinit var binding: ActivityReviewManagementBinding
    private lateinit var dynamoService: DynamoDBService
    private lateinit var ratingService: RatingService
    private lateinit var touristSpotService: TouristSpotService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jwtToken = intent.getStringExtra("JWTTOKEN")
        val idToken = intent.getStringExtra("IDTOKEN")

        // Callback
        dynamoService = DynamoDBService(baseContext) //Aquí ya se ejecuta el dynamoConnectionAndAuth
        dynamoService.setCallback(this)

        binding.GetAllBtn.setOnClickListener(){
            Log.d("GetAllRating", ratingService.getAllRatings().toString())
            Log.d("GetAllSpot", touristSpotService.getAllTouristSpot().toString())
        }

        binding.CreateBtn.setOnClickListener(){
            val rating = RatingDto(1, 1, 4, "Me FASCINO", "2025-02-21T17:42:34Z", "b1ab6510-50a1-70b4-7dd2-492227c8a63d")
            ratingService.createRating(rating)
        }

        binding.UpdateBtn.setOnClickListener(){
            val rating = RatingDto(1, 1, 2, "Un Horror", "2025-02-21T17:42:34Z", "b1ab6510-50a1-70b4-7dd2-492227c8a63d")
            ratingService.update(rating)
        }

        binding.DeleteBtn.setOnClickListener(){
            ratingService.delete(Primitive(1),Primitive(1))
        }
    }

    // El callback que se ejecutará cuando DynamoDB esté listo
    override fun onDynamoDBInitialized() {
        Log.d("Entre", "HI")
        ratingService = RatingService(dynamoService)
        touristSpotService = TouristSpotService(dynamoService)
        queryReviews()
    }

    @SuppressLint("SetTextI18n")
    fun queryReviews() {
        val review: Document? = ratingService.getRatingById(0, 0)
        if (review != null) {
            binding.touristSpot.setText(review.getValue("touristSpotId").toString())
            binding.Review.setText(review.getValue("review").toString())
            binding.Rating.setText(review.getValue("rating").toString())
        }
    }
}
