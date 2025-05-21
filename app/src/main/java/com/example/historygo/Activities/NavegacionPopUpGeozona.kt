package com.example.historygo.Activities

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.example.historygo.Activities.Fragments.ReproductorFragment
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.Helper.GeofenceHelper
import com.example.historygo.Helper.LanguagePreference
import com.example.historygo.R
import com.example.historygo.Services.LightSensorService
import com.example.historygo.clientsdk.HistorygoapiClient
import com.example.historygo.databinding.ActivityNavegacionPopUpGeozonaBinding
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay

class NavegacionPopUpGeozona : BaseActivity() {
    val RADIUS_OF_EARTH_KM = 6371
    private lateinit var binding: ActivityNavegacionPopUpGeozonaBinding
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var map : MapView
    private lateinit var lightSensorService: LightSensorService
    private lateinit var  currentLanguage: String

    // Ubicación del Chorro de Quevedo en Bogotá
    private val chorroLocationLatLng = LatLng(4.5972, -74.0697)
    private val chorroLocation = GeoPoint(4.5972, -74.0697)

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private var lastKnownLocation: GeoPoint? = null
    private val REQUEST_FOREGROUND_LOCATION = 1001
    private val REQUEST_BACKGROUND_LOCATION = 1002
    private val GEOFENCE_ID = "CHORRO_QUEVEDO_ID"

    //Rutas
    private var roadOverlay: Polyline? = null
    lateinit var roadManager: RoadManager


    override fun onCreate(savedInstanceState: Bundle?) {
        //API Gateway
        val factory = ApiClientFactory()
        val client: HistorygoapiClient = factory.build(HistorygoapiClient::class.java)

        val jwtToken = getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("jwt_token", null)
        currentLanguage = LanguagePreference.getLanguage(this)

        // Geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
        askPermission()

        super.onCreate(savedInstanceState)
        binding = ActivityNavegacionPopUpGeozonaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lightSensorService = LightSensorService(this)
        lightSensorService.registerLightSensorListener {
            changeMapColors(it)
        }

        if (intent?.getBooleanExtra("mostrar_popup", false) == true) {
            mostrarPopup()
        }

        Configuration.getInstance().load(this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        //rutas
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        roadManager = OSRMRoadManager(this, "ANDROID")

        // Agregar overlay para mostrar la ubicación del usuario
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        map.overlays.add(locationOverlay)

        centerOnUserLocation { location ->
            if (calculateDistance(
                    location.latitude,
                    location.longitude,
                    chorroLocationLatLng.latitude,
                    chorroLocationLatLng.longitude
                ) > 50
            ) {
                drawRoute(location, chorroLocation)
                Log.d("Ruta mapa", "Dibujé la ruta desde ubicación actual")
            } else {
                Log.d("Ruta mapa", "Estás muy cerca, no dibujo ruta")
            }
        }

        drawGeofenceCircle(chorroLocationLatLng, 25.0)


        if (jwtToken != null) {
            Log.d("mapTest", "token: $jwtToken")
            setupAudioPlayback()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        locationOverlay.enableMyLocation()
        centerOnUserLocation { location ->
            if (calculateDistance(
                    location.latitude,
                    location.longitude,
                    chorroLocationLatLng.latitude,
                    chorroLocationLatLng.longitude
                ) > 50
            ) {
                drawRoute(location, chorroLocation)
                Log.d("Ruta mapa", "Dibujé la ruta desde ubicación actual")
            } else {
                Log.d("Ruta mapa", "Estás muy cerca, no dibujo ruta ${calculateDistance(location.latitude, location.longitude, chorroLocationLatLng.latitude, chorroLocationLatLng.longitude)}")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        lightSensorService.unregisterLightSensorListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_FOREGROUND_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    askPermission() // Llamamos de nuevo para pedir el background
                } else {
                    Toast.makeText(this, "La app necesita ubicación para funcionar correctamente.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            REQUEST_BACKGROUND_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addGeofence(chorroLocationLatLng, 25f)
                } else {
                    Toast.makeText(this, "Sin este permiso, no podremos saber cuando hayas llegado a tu destino.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            else -> {
                // Otros permisos
            }
        }
    }


    private fun centerOnUserLocation(onLocationAvailable: (GeoPoint) -> Unit) {
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
                map.controller.setZoom(15)

                onLocationAvailable(lastLocation)
            } else {
                Toast.makeText(this, R.string.toast_searching_location, Toast.LENGTH_SHORT).show()
            }
        }, 1000)
    }


    private fun askPermission() {
        // Primero verificamos ubicación en primer plano
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_LOCATION
            )

        } else {
            // Ya tenemos foreground, ahora validamos background
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                    // Muestra una explicación al usuario antes de pedir el permiso
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Permiso adicional necesario")
                        .setMessage("Para que la app funcione correctamente incluso cuando no la estés usando, necesitamos permiso de ubicación en segundo plano.")
                        .setPositiveButton("Aceptar") { _, _ ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                REQUEST_BACKGROUND_LOCATION
                            )
                        }
                        .setNegativeButton("Cancelar") { _, _ ->
                            Toast.makeText(this, "La funcionalidad estará limitada.", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                } else {
                    // Ya tiene todos los permisos
                    addGeofence(chorroLocationLatLng, 25f)
                }
            } else {
                // Android < Q, no se requiere background explícitamente
                addGeofence(chorroLocationLatLng, 25f)
            }
        }
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

    //Routes
    fun drawRoute(start : GeoPoint, finish : GeoPoint) {
        val routePoints = ArrayList<GeoPoint>()
        routePoints.add(start)
        routePoints.add(finish)
        val road = roadManager.getRoad(routePoints)
        displayRoad(road)
    }

    private fun displayRoad(road:   Road) {
        Log.i("MapsApp", "Route length: " + road.mLength + " klm")
        Log.i("MapsApp", "Duration: " + road.mDuration / 60 + " min")
        if (roadOverlay != null) {
            map.overlays.remove(roadOverlay)
        }
        roadOverlay = RoadManager.buildRoadOverlay(road)
        roadOverlay!!.outlinePaint.color = Color.RED
        roadOverlay!!.outlinePaint.strokeWidth = 10F
        map.overlays.add(roadOverlay)
    }

    fun calculateDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Long {
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val result = RADIUS_OF_EARTH_KM * c

        // Convertir a metros (1 km = 1000 metros)
        return Math.round(result * 1000.0)
    }

    private fun drawGeofenceCircle(center: LatLng, radiusMeters: Double) {
        val circle = Polygon()
        circle.points = Polygon.pointsAsCircle(
            GeoPoint(center.latitude, center.longitude),
            radiusMeters
        )
        circle.strokeColor = ContextCompat.getColor(this, R.color.red)
        circle.fillColor = Color.TRANSPARENT // <- Sin relleno
        circle.strokeWidth = 4f // puedes ajustar el grosor del borde

        map.overlays.add(circle)
        map.invalidate() // refrescar el mapa
    }

    fun changeMapColors(light: Float){
        if(light<=1){
            map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        }else{
            map.overlayManager.tilesOverlay.setColorFilter(null)
        }
    }

    //REPRODUCTOR AUDIO
    private fun setupAudioPlayback() {
        // Use the helper function to get the URI :
        //val audioUri = getRawUri(this, R.raw.sample_audio)
        //Log.d("MainActivity", "Audio URI: $audioUri") // Log the URI
        val audioName = applicationContext.getString(R.string.audio_name_chorro)   // CAMBIAR A NOMBRE DE AUDIO
        val audioKey: String
        if(currentLanguage == "es"){
            audioKey = "guion-trayecto-chorro.mp3"
        } else {
            audioKey = "guion-trayecto-chorro-en.mp3"
        }
        val cloudFrontBaseUrl = "https://d3krfb04kdzji1.cloudfront.net/"
        val audioUrl = "$cloudFrontBaseUrl$audioKey"


        val fragment = ReproductorFragment.newInstance(audioUrl, audioName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, fragment)
            .commit()
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
            val intent = Intent(this, MenuOpcionesGuia::class.java)
            startActivity(intent)
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