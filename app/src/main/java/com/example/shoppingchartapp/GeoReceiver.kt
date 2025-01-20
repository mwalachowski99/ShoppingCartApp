package com.example.shoppingchartapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val channelId = createChannel(context)
        when (GeofencingEvent.fromIntent(intent).geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val name = GeofencingEvent.fromIntent(intent).triggeringGeofences.first().requestId.replace("geoEnter","")

                val data = workDataOf("title" to "Welcome", "name" to name, "channelId" to channelId)
                val worker = OneTimeWorkRequestBuilder<ShopWorker>().setInputData(data).build()

                WorkManager.getInstance(context).enqueue(worker)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val name = GeofencingEvent.fromIntent(intent).triggeringGeofences.first().requestId.replace("geoExit","")

                val data = workDataOf("title" to "Goodbye", "name" to name, "channelId" to channelId)
                val worker = OneTimeWorkRequestBuilder<ShopWorker>().setInputData(data).build()

                WorkManager.getInstance(context).enqueue(worker)
            }
            else -> {
                Log.e("geofenceApp", "Wrong transition type.")
            }
        }
    }

    private fun createChannel(context: Context): String {
        val id = "ShopChannel"
        val channel = NotificationChannel(
            id,
            "Shop Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
        return id
    }
}