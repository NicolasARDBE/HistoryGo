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
import com.example.historygo.R
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.clientsdk.model.RatingPUT
import com.example.historygo.databinding.ActivityUpdateComentBinding
import kotlinx.coroutines.*
import java.math.BigDecimal

class UpdateComentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateComentBinding
    private lateinit var client: HistorygoapiClient
    private lateinit var cognito: Cognito
    private val jwtDecoder = JWTDecoder()

    private lateinit var stars: List<ImageView>
    private var currentRating = 0

    private var touristSpotId: String? = null
    private var ratingId: String? = null
    private var jwtToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateComentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ApiClientFactory()
        client = factory.build(HistorygoapiClient::class.java)

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

        // Recuperar datos del Intent
        touristSpotId = intent.getStringExtra("TOURIST_SPOT_ID")
        ratingId = intent.getStringExtra("RATING_ID")
        jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        if (jwtToken == null || touristSpotId == null || ratingId == null) {
            Toast.makeText(this, "Datos faltantes para la actualización", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
                configurarBotonActualizar()
            } else {
                Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonActualizar() {
        binding.CreateBtn.setOnClickListener {
            val reviewText = binding.Review.text.toString().trim()
            if (reviewText.isEmpty() || currentRating == 0) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ratingPut = RatingPUT().apply {
                rating = BigDecimal(currentRating)
                review = reviewText
                userId = jwtDecoder.decodeJWTCognitoUsername(jwtToken)
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    client.ratingTableTouristSpotIdRatingIdPut(
                        touristSpotId,
                        ratingId,
                        jwtToken,
                        ratingPut
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UpdateComentActivity, "Comentario actualizado", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@UpdateComentActivity, UpdateDeleteComentActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        finish()
                    }


                } catch (e: Exception) {
                    Log.e("UpdateComentActivity", "Error actualizando: ${e.message}", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UpdateComentActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateStarColors(rating: Int) {
        stars.forEachIndexed { index, imageView ->
            val color = if (index < rating) R.color.yellow else android.R.color.darker_gray
            imageView.setColorFilter(resources.getColor(color, theme))
        }
    }
}
