package com.example.historygo.Services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.historygo.Activities.MenuOpcionesGuia
import com.example.historygo.R

class NotificationService : Service() {
    var notid = 0
    override fun onCreate(){
        super.onCreate()
        Log.i("Nicolas", "Se esta escuchando notificacion")
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "channel"
        val description = "channel description"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("Test", name, importance)
        channel.description = description
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(title: String, message: String, icon: Int, target: Class<*>) : Notification {
        val builder =  NotificationCompat.Builder(this, "Test")
        builder.setSmallIcon(icon)
        builder.setContentTitle(title)
        builder.setContentText(message)
        val intent = Intent(this, target)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true) //Remueve la notificación cuando se toca
        return builder.build()
    }

    fun notify(notification: Notification) {
        notid++
        val notificationManager = NotificationManagerCompat.from(this)
        if(checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notid, notification)
        }
        if(notificationManager.areNotificationsEnabled()){
            notificationManager.notify(notid, notification)
        }else{
            Log.i("Nicolas", "No hay permisos")
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "MyService Completed or Stopped.", Toast.LENGTH_SHORT).show()
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Nicolas", "Entre a start")
        notify(buildNotification("HistoryGO", "Has llegado al Chorro de Quevedo",
            R.drawable.baseline_circle_notifications_24, MenuOpcionesGuia::class.java))
        return START_STICKY
    }
}