package com.example.historygo.Activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityDisplay360DegreeImageBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama

class Display360DegreeImage : BaseActivity() {
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
        val imageUrl = "${cloudFrontBaseUrl}images/chorro-quevedo-360-3.png"
        loadPanoramaImage(imageUrl)

        // API Gateway y reproducción de audio
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)
        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        if (jwtToken != null) {
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
}
