package com.example.historygo.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.historygo.R
import com.example.historygo.databinding.ActivitySelectedExperienceBinding
import androidx.core.net.toUri
import com.example.historygo.Activities.Maps.MapsActivity

class SelectedExperience : AppCompatActivity() {

    //create binding for the activity
    private lateinit var binding: ActivitySelectedExperienceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Carga correctamente el binding
        binding = ActivitySelectedExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Márgenes para la pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.startRoute.setOnClickListener {
            startActivity(Intent(this, NavegacionPopUpGeozona::class.java))
        }


    }



}