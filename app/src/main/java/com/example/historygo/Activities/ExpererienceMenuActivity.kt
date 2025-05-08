package com.example.historygo.Activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Adapters.ExperienceAdapter
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.Helper.LanguagePreference
import com.example.historygo.Model.Experience
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient


class ExpererienceMenuActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExperienceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_expererience_menu)

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        val currentLanguage = LanguagePreference.getLanguage(this)

        // Inicializar el cliente de AWS
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        recyclerView = findViewById(R.id.recyclerViewExperiences)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Hacer la llamada a la API en un hilo separado
        Thread {
            try {
                // Obtener la lista de lugares turísticos desde AWS
                val touristSpotsResponse = client.touristSpotTableGetAllGet(jwtToken)
                val touristSpots = touristSpotsResponse.touristSpots
                Log.d("Exp img", "Mensaje: ${touristSpots[1].imageKey}")
                // Crear una lista de objetos Experience

                val experiences = touristSpots.map { touristSpot ->
                    val description = if (currentLanguage == "en") {
                        touristSpot.descriptionEn
                    } else {
                        touristSpot.description
                    }

                    Experience(
                        touristSpot.touristSpotId.toInt(),
                        touristSpot.name,
                        description,
                        touristSpot.imageKey
                    )
                }

                // Ejecutar el código que actualiza el RecyclerView en el hilo principal
                Handler(Looper.getMainLooper()).post {
                    adapter = ExperienceAdapter(experiences)
                    recyclerView.adapter = adapter
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar el error
            }
        }.start()
    }
}