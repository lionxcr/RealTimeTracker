package com.lionxcr.real.time.tracker.src

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.*

import com.google.gson.Gson

import java.util.Date

class TrackerBackgroundService: IntentService(TrackerBackgroundService::class.java.name) {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private val mGson: Gson

    init {
        mGson = Gson()
    }

    @SuppressLint("MissingPermission")
    override fun onHandleIntent(intent: Intent?) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        mLocationCallback = createLocationRequestCallback()

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest: LocationSettingsRequest = builder.build()

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        Handler(applicationContext.mainLooper).post { mFusedLocationClient!!.requestLocationUpdates(locationRequest, mLocationCallback!!, Looper.myLooper()) }
    }

    private fun createLocationRequestCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                val location = locationResult.locations.last()
                val locationCoordinates = createCoordinates(location.latitude, location.longitude)
                broadcastLocationReceived(locationCoordinates)
                mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            }
        }
    }

    private fun createCoordinates(latitude: Double, longitude: Double): LocationCoordinates {
        return LocationCoordinates(latitude, longitude, Date().time)
    }

    private fun broadcastLocationReceived(locationCoordinates: LocationCoordinates) {
        val eventIntent = Intent(TrackerForegroundService.LOCATION_EVENT_NAME)
        eventIntent.putExtra(TrackerForegroundService.LOCATION_EVENT_DATA_NAME, mGson.toJson(locationCoordinates))
        applicationContext.sendBroadcast(eventIntent)
    }
}
