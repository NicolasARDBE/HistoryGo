package com.example.historygo.Activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Activities.Fragments.ReproductorFragment
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient

class ExperienciaCompletaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //API Gateway
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)
        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_experiencia_completa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (jwtToken != null) {
            setupAudioPlayback(client, jwtToken)
        }
    }

    //REPRODUCTOR AUDIO
    private fun setupAudioPlayback(client: HistorygoapiClient, jwtToken: String) {
        // Use the helper function to get the URI :
        //val audioUri = getRawUri(this, R.raw.sample_audio)
        //Log.d("MainActivity", "Audio URI: $audioUri") // Log the URI
        val audioName = "Chorro de Quevedo"  // CAMBIAR A NOMBRE DE AUDIO

        val audioKey = "guion-v2-chorro.mp3"
        val cloudFrontBaseUrl = "https://d3krfb04kdzji1.cloudfront.net/"
        val audioUrl = "$cloudFrontBaseUrl$audioKey"


        val fragment = ReproductorFragment.newInstance(audioUrl, audioName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
    }
}