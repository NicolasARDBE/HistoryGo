package com.example.historygo.Activities

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.historygo.R
import com.example.historygo.databinding.ActivityNavegacionPopUpGeozonaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class NavegacionPopUpGeozona : AppCompatActivity() {
    private lateinit var binding: ActivityNavegacionPopUpGeozonaBinding
    private lateinit var geocoder: Geocoder
    private lateinit var map : MapView
    private var testLocation = GeoPoint(4.59, -74.06)

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
        mostrarPopup()
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

    fun mostrarPopup() {
        // 1. Infla el layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_popup, null)

        // 2. Crea el diálogo usando MaterialAlertDialogBuilder o AlertDialog.Builder
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        // 3. Referencia los elementos del layout
        val btnDespues = dialogView.findViewById<Button>(R.id.btnDespues)
        val btnIniciar = dialogView.findViewById<Button>(R.id.btnIniciar)

        // 4. Configura eventos de clic
        btnDespues.setOnClickListener {
            // Acciones para "Después"
            dialog.dismiss()
        }
        btnIniciar.setOnClickListener {
            //Binding a menu opciones guia
            val intent = android.content.Intent(this, MenuOpcionesGuia::class.java)
            startActivity(intent)
            dialog.dismiss()
            // Por ejemplo: iniciar otra Activity o lo que necesites
        }

        // 5. Muestra el diálogo
        dialog.show()
    }
}