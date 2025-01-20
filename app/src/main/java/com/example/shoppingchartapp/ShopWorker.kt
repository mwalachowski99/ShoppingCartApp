package com.example.shoppingchartapp

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ShopWorker (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        try{
            val title = inputData.getString("title")
            val channelId = inputData.getString("channelId") ?: ""
            val name = inputData.getString("name")

            val notification = NotificationCompat.Builder(
                applicationContext,
                channelId
            ).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(name)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(0, notification)
        }
        catch (e: java.lang.Exception){
            return Result.failure()
        }

        return Result.success()
    }
}