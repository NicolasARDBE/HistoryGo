package com.example.historygo.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Adapters.UpdateDeleteCommentAdapter
import com.example.historygo.AwsServices.Cognito
import com.example.historygo.AwsServices.CognitoManager
import com.example.historygo.AwsServices.DynamoDBInitializationCallback
import com.example.historygo.AwsServices.DynamoDBService
import com.example.historygo.Model.ComentarioExperiencia
import com.example.historygo.R
import com.example.historygo.Services.JWTDecoder
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityUpdateDeleteComentBinding

class UpdateDeleteComentActivity : AppCompatActivity(), DynamoDBInitializationCallback {
    private lateinit var binding: ActivityUpdateDeleteComentBinding
    private lateinit var dynamoService: DynamoDBService
    private lateinit var cognito: Cognito
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UpdateDeleteCommentAdapter
    private val jwtDecoder = JWTDecoder()
    private lateinit var jwtToken: String
    private lateinit var client: HistorygoapiClient

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDeleteComentBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_update_delete_coment)

        val factory = ApiClientFactory()
        client = factory.build(HistorygoapiClient::class.java)

        recyclerView = findViewById(R.id.recyclerComentarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Callback
        dynamoService = DynamoDBService(baseContext)
        dynamoService.setCallback(this)


        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        jwtToken = sharedPreferences.getString("jwt_token", null) ?: ""

        if (jwtToken.isNullOrEmpty()) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = jwtDecoder.decodeJWTCognitoUsername(jwtToken)
        Log.d("UpdateDeleteComent", "Usuario autenticado: $userId")




        Thread {
            try {
                val response = client.ratingTableGetAllGet(jwtToken)
                val allRatings = response.ratings
                val userComments = allRatings.filter { it.userId == userId }


                val comentarios = userComments.map {
                    ComentarioExperiencia(
                        it.rating.toInt(),
                        it.review,
                        it.timestamp,
                        it.name,
                        it.userId,
                        it.ratingId,
                        it.touristSpotId.toInt()
                    )
                }.toMutableList()

                for (rating in userComments) {
                    Log.d("RATINGID", "ratingId: ${rating.ratingId}")
                    Log.d("PRUEBA", "general: ${userComments}")
                    Log.d("PRUEBA2", "id lugar: ${rating.touristSpotId}")
                    Log.d("PRUEBA3", "rating: ${rating.rating}")
                    Log.d("PRUEBA4", "comentario: ${rating.review}")
                    Log.d("TIME", "tiempo: ${rating.timestamp}")
                    Log.d("USUARIO", "Usuario: ${rating.userId}")
                    Log.d("NOMBRE", "name: ${rating.name}")
                }

                Handler(Looper.getMainLooper()).post {
                    adapter = UpdateDeleteCommentAdapter(comentarios) { touristSpotId, ratingId ->
                        deleteComment(touristSpotId.toString(), ratingId.toString())
                    }
                    recyclerView.adapter = adapter
                }

            } catch (e: Exception) {
                Log.e("UpdateDeleteComent", "Error al obtener comentarios", e)
            }
        }.start()

        // Obtener instancia de Cognito si se necesita en el futuro
        CognitoManager.getInstance(this) { cognitoInstance ->
            if (cognitoInstance != null) {
                cognito = cognitoInstance
            } else {
                Log.e("UpdateDeleteComent", "Error: Cognito no disponible")
            }
        }
    }

    override fun onDynamoDBInitialized() {
        // Aquí puedes colocar lógica si es necesario cuando se inicializa DynamoDB
    }

    private fun deleteComment(touristSpotId: String, ratingId: String) {
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        // Usa el token que ya habías leído correctamente desde SharedPreferences
        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("jwt_token", null) ?: ""

        Thread {
            try {
                client.ratingTableTouristSpotIdRatingIdDelete(touristSpotId, ratingId, jwtToken)
                runOnUiThread {
                    Toast.makeText(this, "Comentario eliminado", Toast.LENGTH_SHORT).show()
                    adapter.removeComment(ratingId)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show()
                    Log.e("DeleteComment", "Error: ", e)
                }
            }
        }.start()
    }

}
