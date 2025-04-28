package com.example.historygo.Activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Activities.Fragments.ReproductorFragment
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityDisplay360DegreeImageBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.utils.PLUtils

class Display360DegreeImage : AppCompatActivity() {
    private lateinit var binding: ActivityDisplay360DegreeImageBinding
    private lateinit var plManager: PLManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //API Gateway
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        super.onCreate(savedInstanceState)
        binding = ActivityDisplay360DegreeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.flView

        initializePlManager()

        val panoroma = PLSphericalPanorama()
        panoroma.camera.lookAt(30.0f, 90.0f)
        panoroma.setImage(PLImage(PLUtils.getBitmap(this, R.raw.test2)))
        plManager.panorama = panoroma

        if(jwtToken != null){
            setupAudioPlayback(client, jwtToken)
        }
    }


    private fun initializePlManager(){
        plManager = PLManager(this).apply {
            setContentView(binding.flView)
            onCreate()
            startSensorialRotation()
            /*isScrollingEnabled = true
            isAccelerometerEnabled = false
            isZoomEnabled = true
            isInertiaEnabled = true
            isAcceleratedTouchScrollingEnabled = false*/
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return plManager.onTouchEvent(event!!)
    }

    override fun onResume() {
        super.onResume()
        plManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        plManager.onDestroy()
    }

    //REPRODUCTOR AUDIO
    private fun setupAudioPlayback(client: HistorygoapiClient, jwtToken: String) {
        // Use the helper function to get the URI :
        //val audioUri = getRawUri(this, R.raw.sample_audio)
        //Log.d("MainActivity", "Audio URI: $audioUri") // Log the URI
        val audioName = "Chorro de Quevedo"  // CAMBIAR A NOMBRE DE AUDIO

        val audioKey = "guion-trayecto-chorro.mp3"
        val cloudFrontBaseUrl = "https://d3krfb04kdzji1.cloudfront.net/"
        val audioUrl = "$cloudFrontBaseUrl$audioKey"


        val fragment = ReproductorFragment.newInstance(audioUrl, audioName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
    }
}