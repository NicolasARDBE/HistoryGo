package com.example.historygo.Activities

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.historygo.databinding.ActivityNavegacionPopUpGeozonaBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class NavegacionPopUpGeozona : AppCompatActivity() {
    private lateinit var binding: ActivityNavegacionPopUpGeozonaBinding
    private lateinit var geocoder: Geocoder
    lateinit var map : MapView
    var testLocation = GeoPoint(4.59, -74.06)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavegacionPopUpGeozonaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Configuration.getInstance().load(this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        geocoder = Geocoder(this)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        map.controller.setZoom(15.0)
        map.controller.animateTo(testLocation)
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}