package com.example.historygo.GeofenceReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.historygo.Activities.NavegacionPopUpGeozona
import com.example.historygo.Services.NotificationService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.d(TAG, "onReceive: Error receiving geofence event...")
                return
            }
        } else{
            Log.d(TAG, "Voy NULL")
        }

        val geofenceList = geofencingEvent?.triggeringGeofences
        if (geofenceList != null) {
            for (geofence in geofenceList) {
                Log.d(TAG, "onReceive: ${geofence.requestId}")
            }
        }

        // val location: Location = geofencingEvent.triggeringLocation
        val transitionType = geofencingEvent?.geofenceTransition
        var message: String = null.toString()
         when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                message = "You have entered the Chorro de Quevedo area"
                val intent = Intent(context, NavegacionPopUpGeozona::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("mostrar_popup", true)
                }
                context.startActivity(intent)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                message = "You have left the Chorro de Quevedo area"
            }
        }

        val serviceIntent = Intent(context, NotificationService::class.java).apply {
            putExtra("notification_message", message)
        }
        context.startService(serviceIntent)
    }
}
