package com.example.historygo.GeofenceReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import com.example.historygo.Activities.MenuOpcionesGuia
import com.example.historygo.R
import com.example.historygo.Services.NotificationService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
                message =  "Has entrado a la zona del Chorro de Quevedo"
                val intent = Intent(context, MenuOpcionesGuia::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("mostrar_popup", true)
                }
                context.startActivity(intent)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                message = "Has salido de la zona del Chorro de Quevedo"
            }
            else -> null
        }

        if (message != null) {
            val serviceIntent = Intent(context, NotificationService::class.java).apply {
                putExtra("notification_message", message)
            }
            context.startService(serviceIntent)
        }
    }
}
