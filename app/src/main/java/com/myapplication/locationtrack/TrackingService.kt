package com.myapplication.locationtrack

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.location.*

class TrackingService : Service() {

    private var notificationBuilder: NotificationBuilder? = null

    override fun onBind(intent: Intent?): IBinder? {
        return TrackingServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        getNotificationBuilder().showNotification(true)
        startLocationUpdates()
    }

    private fun getNotificationBuilder() : NotificationBuilder {
        if(notificationBuilder == null) {
            notificationBuilder = NotificationBuilder(this)
        }
        return notificationBuilder!!
    }

    inner class TrackingServiceBinder: Binder() {
        fun getTrackingService(): TrackingService {
            return this@TrackingService
        }
    }

    fun startLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 100

        val settings = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(settings)

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, object: LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    result?.let {
                        onLocationChanged(it.lastLocation)
                    }

                }
            }, Looper.myLooper())
    }

    fun onLocationChanged(location: Location) {
        Toast.makeText(applicationContext, location.latitude.toString(), Toast.LENGTH_SHORT).show()
    }
}