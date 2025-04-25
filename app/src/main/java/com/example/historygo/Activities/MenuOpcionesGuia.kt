package com.example.historygo.Activities

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.historygo.Helper.GeofenceHelper
import com.example.historygo.R
import com.example.historygo.databinding.ActivityMenuOpcionesGuiaBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MenuOpcionesGuia : AppCompatActivity() {

    private lateinit var binding: ActivityMenuOpcionesGuiaBinding
    private lateinit var geocoder: Geocoder
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var map: MapView

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private val GEOFENCE_ID = "CHORRO_QUEVEDO_ID"

    private var lastKnownLocation: GeoPoint? = null
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    // Ubicación del Chorro de Quevedo en Bogotá
    private val chorroLocation = GeoPoint(4.5972, -74.0697)
    private val chorroLocationLatLng = LatLng(4.5972, -74.0697)

    override fun onCreate(savedInstanceState: Bundle?) {
        //val newIntentService = Intent(this, NotificationService::class.java)
        //startService(newIntentService)

        // Geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)

        askPermission()

        super.onCreate(savedInstanceState)
        binding = ActivityMenuOpcionesGuiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent?.getBooleanExtra("mostrar_popup", false) == true) {
            mostrarPopup()
        }

        // Inicializar mapa
        Configuration.getInstance().load(
            this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        geocoder = Geocoder(this)

        // Agregar overlay para mostrar la ubicación del usuario
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        centerOnUserLocation()
        locationOverlay.enableFollowLocation()


        //botones de opciones de menu
        binding.ExpCompletaBtn.setOnClickListener {
            val intent = Intent(this, ExperienciaCompletaActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        locationOverlay.enableMyLocation()
        centerOnUserLocation()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun askPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                addGeofence(chorroLocationLatLng, 50f)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }

            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido, no hacer nada adicional
                } else {
                    finish()
                    Toast.makeText(this, "Funcionalidades reducidas", Toast.LENGTH_LONG).show()
                }
                return
            }

            else -> {
                // Ignorar todas las demás solicitudes
            }
        }
    }

    private fun centerOnUserLocation() {
        val handler = Handler()
        handler.postDelayed({
            val lastLocation = locationOverlay.myLocation
            if (lastLocation != null) {
                if (lastKnownLocation != null) {
                    val distance = lastKnownLocation!!.distanceToAsDouble(lastLocation)
                    if (distance > 30) {
                        lastKnownLocation = lastLocation
                    }
                } else {
                    lastKnownLocation = lastLocation
                }

                map.controller.setCenter(lastLocation)
                map.controller.setZoom(18)
            } else {
                Toast.makeText(this, "Buscando ubicación...", Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }

    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or
                    Geofence.GEOFENCE_TRANSITION_DWELL or
                    Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent = geofenceHelper.getPendingIntent()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d("MenuOpcionesGuia", "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d("MenuOpcionesGuia", "onFailure: $errorMessage")
            }
    }

    private fun mostrarPopup() {
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
            // Acciones para "Iniciar"
            dialog.dismiss()
            // Por ejemplo: iniciar otra Activity o lo que necesites
        }

        // 5. Muestra el diálogo
        dialog.show()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // actualiza el intent actual

        if (intent.getBooleanExtra("mostrar_popup", false)) {
            mostrarPopup()
        }
    }
}