package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.bumptech.glide.Glide
import com.example.historygo.Adapters.CommentAdapter
import com.example.historygo.Adapters.CommentAdapter.ViewHolder
import com.example.historygo.Adapters.ExperienceAdapter
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.Model.ComentarioExperiencia
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient

class FeedbackActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter

    private lateinit var scoreTextView: TextView
    private lateinit var reviewsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Botón que redirige a activity_single_coment.xml
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

        // Hacer la llamada a la API en un hilo separado
        Thread {
            try {
                val ratingResponse = client.ratingTableGetAllGet(jwtToken)
                val ratingComment = ratingResponse.ratings

                for (rating in ratingComment) {
                    Log.d("RATINGID", "ratingId: ${rating.ratingId}")
                    Log.d("PRUEBA", "general: ${ratingComment}")
                    Log.d("PRUEBA2", "id lugar: ${rating.touristSpotId}")
                    Log.d("PRUEBA3", "rating: ${rating.rating}")
                    Log.d("PRUEBA4", "comentario: ${rating.review}")
                    Log.d("TIME", "tiempo: ${rating.timestamp}")
                    Log.d("USUARIO", "Usuario: ${rating.userId}")
                }

                val experiencia = ratingComment.map { rating ->
                    ComentarioExperiencia(
                        rating.rating.toInt(),                // rating: Int
                        rating.review,                        // review: String
                        rating.timestamp,                     // timestamp: String
                        rating.userId,                        // userName: String (puedes cambiar si es otro campo)
                        rating.ratingId,                      // ratingId: String
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

                Log.d("Promedio", "Total: $total | Suma: $sumatoria | promedio: $promedio")

                // 4) Actualizas UI en el hilo principal
                Handler(Looper.getMainLooper()).post {
                    adapter = CommentAdapter(experiencia)
                    recyclerView.adapter = adapter
                    // Mostrar el promedio de puntuaciones con una cifra decimal
                    scoreTextView.text = String.format("%.1f", promedio)

                    // Mostrar el total de comentarios
                    reviewsTextView.text = "$total reseñas"
                    Log.d("Tamano", "Objeto: ${experiencia.size}")
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al obtener o procesar ratings", e)
                // Manejar el error (puedes mostrar un mensaje de error si lo deseas)
            }
        }.start()
    }

}
