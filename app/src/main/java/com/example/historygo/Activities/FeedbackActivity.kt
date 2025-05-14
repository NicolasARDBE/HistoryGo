package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Adapters.CommentAdapter
import com.example.historygo.Model.ComentarioExperiencia
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient

class FeedbackActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter

    private lateinit var scoreTextView: TextView
    private lateinit var reviewsTextView: TextView
    private lateinit var stars: List<ImageView>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Botón que redirige a SingleComentActivity
        val registerBtn: View = findViewById(R.id.RegisterBtn)
        registerBtn.setOnClickListener {
            val intent = Intent(this, SingleComentActivity::class.java)
            startActivity(intent)
        }

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        // Inicializar el cliente de AWS
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        recyclerView = findViewById(R.id.commentsContainer)
        recyclerView.layoutManager = LinearLayoutManager(this)

        scoreTextView = findViewById(R.id.score)
        reviewsTextView = findViewById(R.id.reviews)

        // Inicializar las estrellas
        stars = listOf(
            findViewById(R.id.starss1),
            findViewById(R.id.starss2),
            findViewById(R.id.starss3),
            findViewById(R.id.starss4),
            findViewById(R.id.starss5)
        )

        // Llamada a la API
        Thread {
            try {
                val ratingResponse = client.ratingTableGetAllGet(jwtToken)
                val ratingComment = ratingResponse.ratings

                val experiencia = ratingComment.map { rating ->
                    ComentarioExperiencia(
                        rating.rating.toInt(),
                        rating.review,
                        rating.timestamp,
                        rating.name,
                        rating.userId,
                        rating.ratingId,
                        rating.touristSpotId.toInt()
                    )
                }

                val total = experiencia.size
                val sumatoria = experiencia.sumOf {
                    try {
                        it.rating.toDouble()
                    } catch (e: Exception) {
                        Log.e("ParseError", "No se pudo convertir rating: ${it.rating}", e)
                        0.0
                    }
                }
                val promedio = if (total > 0) sumatoria / total else 0.0

                Log.d("Promedio", "Total: $total | Suma: $sumatoria | Promedio: $promedio")

                Handler(Looper.getMainLooper()).post {
                    adapter = CommentAdapter(experiencia)
                    recyclerView.adapter = adapter

                    // Mostrar promedio
                    scoreTextView.text = String.format("%.1f", promedio)

                    // Mostrar número de reseñas
                    reviewsTextView.text = "$total reseñas"

                    // Pintar estrellas
                    val scoreInt = promedio.toInt()
                    for (i in stars.indices) {
                        if (i < scoreInt) {
                            stars[i].setColorFilter(
                                ContextCompat.getColor(this, android.R.color.holo_orange_light)
                            )
                        } else {
                            stars[i].setColorFilter(
                                ContextCompat.getColor(this, android.R.color.darker_gray)
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener o procesar ratings", e)
            }
        }.start()
    }
}
