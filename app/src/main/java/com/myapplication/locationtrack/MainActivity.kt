package com.myapplication.locationtrack

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class MainActivity : AppCompatActivity() {

    private var locationRequest = LocationRequest()
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync {
            loadMap(it)
        }
    }

    private fun loadMap(map: GoogleMap) {
        this.map = map
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object: PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    getMyLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                }

            }).check()
    }

    fun getMyLocation() {
        map.setMyLocationEnabled(true)
        val locationClient: FusedLocationProviderClient = getFusedLocationProviderClient(this)
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                startLocationUpdates()
                location?.let { onLocationChanged(it) }
                val intent = Intent(applicationContext, TrackingService::class.java)
                startService(intent)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun onLocationChanged(it: Location) {
        val latLng = LatLng(it.getLatitude(), it.getLongitude())
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        map.animateCamera(cameraUpdate)
    }

    fun startLocationUpdates() {
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 100

        val settings = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()
        val settingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(settings)

        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, object: LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result?.let {
                    onLocationChanged(it.lastLocation)
                }

            }
        }, Looper.myLooper())
    }
}
