package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.AwsServices.DynamoDBInitializationCallback
import com.example.historygo.AwsServices.DynamoDBService
import com.example.historygo.R
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.clientsdk.model.RatingPOST
import com.example.historygo.databinding.ActivitySingleComentBinding
import java.math.BigDecimal
import kotlinx.coroutines.*


class SingleComentActivity : AppCompatActivity(), DynamoDBInitializationCallback {

    private lateinit var binding: ActivitySingleComentBinding
    private lateinit var dynamoService: DynamoDBService
    private lateinit var cognito: Cognito
    private val jwtDecoder: JWTDecoder = JWTDecoder()
    private lateinit var stars: List<ImageView>
    private var currentRating: Int = 0
    private lateinit var client: HistorygoapiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleComentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ApiClientFactory()
        client = factory.build(HistorygoapiClient::class.java)

        // Inicializar estrellas
        stars = listOf(
            findViewById(R.id.stars1),
            findViewById(R.id.stars2),
            findViewById(R.id.stars3),
            findViewById(R.id.stars4),
            findViewById(R.id.stars5)
        )

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                currentRating = index + 1
                updateStarColors(currentRating)
            }
        }

        // Inicializar DynamoDB y Cognito
        dynamoService = DynamoDBService(baseContext)
        dynamoService.setCallback(this)

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
                configurarBoton()
            } else {
                Log.e("SingleComentActivity", "Error: Cognito no disponible")
                Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBoton() {
        binding.CreateBtn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
            val jwtToken = sharedPreferences.getString("jwt_token", null)

            if (jwtToken.isNullOrEmpty()) {
                Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentRating == 0) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reviewText = binding.Review.text.toString().trim()
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Escribe una reseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ratingPost = RatingPOST().apply {
                touristSpotId = "1"
                rating = BigDecimal(currentRating)
                review = reviewText
                name = jwtDecoder.decodeJWTCognitoFamilyName(jwtToken)
                userId = jwtDecoder.decodeJWTCognitoUsername(jwtToken)
            }

            //  Corrutina para no bloquear el hilo principal
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("SingleComentActivity", "Enviando RatingPOST con JWT: $jwtToken")
                    Log.d("SingleComentActivity", "Datos: rating=${ratingPost.rating}, review=${ratingPost.review}, userId=${ratingPost.userId}, touristSpotId=${ratingPost.touristSpotId}")

                    val response = client.ratingTablePost(jwtToken, ratingPost)

                    withContext(Dispatchers.Main) {
                        if (response != null) {
                            Toast.makeText(this@SingleComentActivity, "Comentario guardado correctamente", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@SingleComentActivity, FeedbackActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            finish()
                        } else {
                            Log.e("SingleComentActivity", "Error: La respuesta no contiene ratingId")
                            Toast.makeText(this@SingleComentActivity, "Error: No se guardó la reseña", Toast.LENGTH_SHORT).show()
                        }
                    }


                } catch (e: Exception) {
                    Log.e("SingleComentActivity", "Error al enviar comentario: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SingleComentActivity, "Error al enviar comentario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    private fun updateStarColors(rating: Int) {
        stars.forEachIndexed { index, imageView ->
            val color = if (index < rating) {
                R.color.yellow
            } else {
                android.R.color.darker_gray
            }
            imageView.setColorFilter(resources.getColor(color, theme))
        }
    }

    override fun onDynamoDBInitialized() {
        // Lógica adicional si es necesario
    }
}
