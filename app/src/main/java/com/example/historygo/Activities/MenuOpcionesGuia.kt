package com.example.historygo.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.historygo.databinding.ActivityMenuOpcionesGuiaBinding
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MenuOpcionesGuia : AppCompatActivity() {

    private lateinit var binding: ActivityMenuOpcionesGuiaBinding
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var map: MapView

    // Ubicación del Chorro de Quevedo en Bogotá
    private val chorroLocation = GeoPoint(4.5972, -74.0697)
    private val chorroLocationLatLng = LatLng(4.5972, -74.0697)

    override fun onCreate(savedInstanceState: Bundle?) {
        //val newIntentService = Intent(this, NotificationService::class.java)
        //startService(newIntentService)

        super.onCreate(savedInstanceState)
        binding = ActivityMenuOpcionesGuiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar mapa
        Configuration.getInstance().load(
            this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)


        //botones de opciones de menu
        binding.ExpCompletaBtn.setOnClickListener {
            val intent = Intent(this, ExperienciaCompletaActivity::class.java)
            startActivity(intent)
        }

        binding.RepAudioBtn.setOnClickListener{

        }

        binding.RepVideosBtn.setOnClickListener{

        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        map.controller.setZoom(21.0)
        map.controller.animateTo(chorroLocation)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}