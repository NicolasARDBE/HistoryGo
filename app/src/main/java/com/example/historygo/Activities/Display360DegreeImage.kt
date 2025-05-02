package com.example.historygo.Activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.historygo.Activities.Fragments.ReproductorFragment
import com.example.historygo.R
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityDisplay360DegreeImageBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama

class Display360DegreeImage : AppCompatActivity() {
    private lateinit var binding: ActivityDisplay360DegreeImageBinding
    private lateinit var plManager: PLManager
    private val cloudFrontBaseUrl = "https://d3krfb04kdzji1.cloudfront.net/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar layout
        binding = ActivityDisplay360DegreeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar visor panorámico
        initializePlManager()

        // Cargar imagen remota 360 desde CloudFront usando Glide
        val imageUrl = "${cloudFrontBaseUrl}images/chorro-quevedo-360-2.jpeg"
        loadPanoramaImage(imageUrl)

        // API Gateway y reproducción de audio
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)
        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        if (jwtToken != null) {
            setupAudioPlayback(client, jwtToken)
        }
    }

    private fun initializePlManager() {
        plManager = PLManager(this).apply {
            setContentView(binding.flView)
            onCreate()
            startSensorialRotation()
        }
    }

    private fun loadPanoramaImage(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val panorama = PLSphericalPanorama()
                    panorama.camera.lookAt(15.0f, 10.0f)
                    panorama.camera.setFov(60.0f, true)
                    panorama.setImage(PLImage(resource))
                    plManager.panorama = panorama
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    // Mensaje Error
                }
            })
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

    private fun setupAudioPlayback(client: HistorygoapiClient, jwtToken: String) {
        val audioName = "Chorro de Quevedo"
        val audioKey = "guion-trayecto-chorro.mp3"
        val audioUrl = "$cloudFrontBaseUrl$audioKey"

        val fragment = ReproductorFragment.newInstance(audioUrl, audioName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
    }
}
