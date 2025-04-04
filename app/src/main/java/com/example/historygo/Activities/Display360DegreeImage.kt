package com.example.historygo.Activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.R
import com.example.historygo.databinding.ActivityDisplay360DegreeImageBinding
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.utils.PLUtils

class Display360DegreeImage : AppCompatActivity() {
    private lateinit var binding: ActivityDisplay360DegreeImageBinding
    private lateinit var plManager: PLManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplay360DegreeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.flView

        initializePlManager()

        val panoroma = PLSphericalPanorama()
        panoroma.camera.lookAt(30.0f, 90.0f)
        panoroma.setImage(PLImage(PLUtils.getBitmap(this, R.raw.test2)))
        plManager.panorama = panoroma
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
}