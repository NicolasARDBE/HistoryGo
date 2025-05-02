package com.example.historygo.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.historygo.R
import com.example.historygo.databinding.ActivitySelectedExperienceBinding
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.bumptech.glide.Glide
import com.example.historygo.clientsdk.HistorygoapiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class SelectedExperience : AppCompatActivity() {

    //create binding for the activity
    private lateinit var binding: ActivitySelectedExperienceBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelectedExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)

        // Desactiva botón si no es el Chorro de Quevedo
        if (id != 0) {
            binding.startRoute.isEnabled = false
            binding.startRoute.setTextColor(Color.LTGRAY)
            binding.startRoute.text = "Pronto Disponible"
        }

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)

        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        // Corrutina para llamada a red
        lifecycleScope.launch {
            try {
                val touristSpotSelected = withContext(Dispatchers.IO) {
                    client.touristSpotTableTouristSpotIdGet(id.toString(), jwtToken)
                }

                // Actualización de la UI en el hilo principal
                binding.tvTitle.text = touristSpotSelected.item.name.s
                binding.tvLocation.text = touristSpotSelected.item.address.s
                binding.textView.text = touristSpotSelected.item.description.s

                val imageUrl = "https://d3krfb04kdzji1.cloudfront.net/${touristSpotSelected.item.imageKey.s}"
                Glide.with(this@SelectedExperience)
                    .load(imageUrl)
                    .placeholder(R.drawable.carga)
                    .error(R.drawable.error)
                    .into(binding.ivExperience)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.startRoute.setOnClickListener {
            mostrarPopup()
        }

        binding.imageButton.setOnClickListener {
            // Abre la dirección en Google Maps
            lifecycleScope.launch {
                val encodedAddress = withContext(Dispatchers.IO) {
                    URLEncoder.encode(binding.tvLocation.text.toString(), "UTF-8")
                }
                val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedAddress")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                val chooser = Intent.createChooser(mapIntent, application.getString(R.string.open_with))
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(chooser)
                }
            }
        }
    }
    fun mostrarPopup() {
        // 1. Infla el layout
        val dialogView = layoutInflater.inflate(R.layout.security_recommendations, null)

        // 2. Crea el diálogo usando MaterialAlertDialogBuilder o AlertDialog.Builder
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        // 3. Referencia los elementos del layout
        val btnContinuar = dialogView.findViewById<Button>(R.id.btnContinuar)

        // 4. Configura eventos de clic
        btnContinuar.setOnClickListener {
            // Acciones para "Después"
            startActivity(Intent(this, NavegacionPopUpGeozona::class.java))
        }

        // 5. Muestra el diálogo
        dialog.show()
    }
}