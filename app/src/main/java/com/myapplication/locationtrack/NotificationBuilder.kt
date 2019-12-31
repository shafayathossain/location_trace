package com.myapplication.locationtrack

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationBuilder(val context: Context){

    companion object {
        const val NOTIFICATION = 0
        const val NOTIFICATION_CHANNEL = "tracking channel"

    }
    private var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var notification : Notification
    private var notificationBuilder: NotificationBuilder? = null


    private fun buildNotification(isOngoing: Boolean): Notification {

        val launcherActivityIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_NO_CREATE
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        notification =  builder
            .setContentTitle("")
            .setContentText("")
            .setContentIntent(launcherActivityIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(isOngoing)
            .build()
        return notification
    }

    fun showNotification(isOngoing: Boolean) {

        if (shouldCreateChannel()) {
            createChannel()
        }
        notificationManager.notify(NOTIFICATION, buildNotification(isOngoing))
    }


    fun clearNotification() {
        notificationManager.cancelAll()
    }

    private fun shouldCreateChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isChannelExists() =
        notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL,
            NOTIFICATION_CHANNEL,
            NotificationManager.IMPORTANCE_HIGH)
            .apply {
                description = "Tracking location"
            }

        notificationManager.createNotificationChannel(notificationChannel)
    }


}