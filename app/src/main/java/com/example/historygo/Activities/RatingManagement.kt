package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.Services.NotificationService
import com.example.historygo.Services.RatingService
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.AwsServices.DynamoDBInitializationCallback
import com.example.historygo.AwsServices.DynamoDBService
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.clientsdk.model.RatingPOST
import com.example.historygo.clientsdk.model.RatingPUT
import com.example.historygo.databinding.ActivityReviewManagementBinding
import java.math.BigDecimal

class RatingManagement : AppCompatActivity(), DynamoDBInitializationCallback {
    private lateinit var binding: ActivityReviewManagementBinding
    private lateinit var dynamoService: DynamoDBService
    private lateinit var ratingService: RatingService
    private lateinit var cognito: Cognito
    private val jwtDecoder: JWTDecoder = JWTDecoder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)
        val jwtToken = intent.getStringExtra("JWTTOKEN")
        val rating = client.ratingTableTouristSpotIdRatingIdGet("456","9331e268-db73-4280-9b08-3cb6832db83b", jwtToken)

        binding.touristSpot.setText(rating.item.touristSpotId.n)
        binding.Review.setText(rating.item.rating.n)
        binding.Rating.setText(rating.item.review.s)

        // Callback
        dynamoService = DynamoDBService(baseContext) //Aquí ya se ejecuta el dynamoConnectionAndAuth
        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("MiActivity", "Error: Cognito no disponible")
            }
        }
        dynamoService.setCallback(this)

        Log.d("JWT", "token: $jwtToken")

        val response1 = client.ratingTableGetAllGet(jwtToken)
        val response2 = client.touristSpotTableGetAllGet(jwtToken)

        binding.GetAllBtn.setOnClickListener{
            Log.d("GetAllRating", "Response: $response1")
            response1.ratings?.forEach { rating ->
                Log.d("GetAllRating", "Rating Item: $rating")
            }

            Log.d("GetAllSpot", "Response: $response2")
            response2.touristSpots?.forEach { rating ->
                Log.d("GetAllSpot", "Rating Item: $rating")
            }
        }

        binding.CreateBtn.setOnClickListener{
            val rating = RatingPOST()
            rating.touristSpotId = "1"
            rating.rating = BigDecimal(1)
            rating.review = "Me FASCINO"
            rating.userId = jwtDecoder.decodeJWTCognitoUsername(jwtToken)
            client.ratingTablePost(jwtToken, rating)
        }

        binding.UpdateBtn.setOnClickListener{
            val rating = RatingPUT()
            rating.rating = BigDecimal(1)
            rating.review = "Me gustó la experiencia"
            rating.userId = jwtDecoder.decodeJWTCognitoUsername(jwtToken)
            client.ratingTableTouristSpotIdRatingIdPut("123","da00444d-fb5e-43be-8245-e2ab58c5dee5",jwtToken, rating)
        }

        binding.DeleteBtn.setOnClickListener{
            client.ratingTableTouristSpotIdRatingIdDelete("123", "da00444d-fb5e-43be-8245-e2ab58c5dee5", jwtToken)
        }

        binding.signOutBtn.setOnClickListener{
            signOut()
            val i = Intent(this, Login::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
        binding.s3Btn.setOnClickListener{
            val intent = Intent(this, S3RetrieveActivity::class.java)
            intent.putExtra("JWTTOKEN", jwtToken)
            startActivity(intent)
        }
    }

    // El callback que se ejecutará cuando DynamoDB esté listo
    override fun onDynamoDBInitialized() {
        //Log.d("Entre", "HI")
        //ratingService = RatingService(dynamoService)
        //touristSpotService = TouristSpotService(dynamoService)
        //queryReviews()
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

    private fun signOut(){
        //Cierra sesión localmente
        cognito.cognitoCachingCredentialsProvider.clear()
        val serviceIntent = Intent(this, NotificationService::class.java)
        stopService(serviceIntent)

        val sharedPreferences = getSharedPreferences("mi_app_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val sharedPreferencesJwt = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val editorJwt = sharedPreferencesJwt.edit()
        editorJwt.remove("jwt_token")
        editorJwt.apply()

        cognito.UserSignOut()
    }
}
