package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.historygo.R
import com.example.historygo.databinding.ActivitySelectedExperienceBinding
import androidx.core.net.toUri
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.bumptech.glide.Glide
import com.example.historygo.Activities.Maps.MapsActivity
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.clientsdk.model.TouristSpotGet
import java.net.URLEncoder

class SelectedExperience : AppCompatActivity() {

    //create binding for the activity
    private lateinit var binding: ActivitySelectedExperienceBinding


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Carga correctamente el binding
        binding = ActivitySelectedExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)

        //Si no es el Chorro de Quevedo no es posible iniciar la ruta
        if(id != 0){
            binding.startRoute.isEnabled = false
            binding.startRoute.setTextColor(Color.LTGRAY)
            binding.startRoute.setText("Pronto Disponible")
        }

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)
        // Inicializar el cliente de AWS
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        val touristSpotSelected = client.touristSpotTableTouristSpotIdGet(id.toString(), jwtToken)

        binding.tvTitle.setText(touristSpotSelected.item.name.s)
        binding.tvLocation.setText(touristSpotSelected.item.address.s)
        binding.textView.setText(touristSpotSelected.item.description.s)

        val imageUrl = "https://d3krfb04kdzji1.cloudfront.net/${touristSpotSelected.item.imageKey.s}"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.carga)
            .error(R.drawable.error)
            .into(binding.ivExperience)


        // Márgenes para la pantalla completa
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.startRoute.setOnClickListener {
            startActivity(Intent(this, NavegacionPopUpGeozona::class.java))
        }

        binding.imageButton.setOnClickListener{
            val encodedAddress = URLEncoder.encode(touristSpotSelected.item.address.s, "UTF-8")
            val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            val chooser = Intent.createChooser(mapIntent, "Abrir con...")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            }
        }
    }
}