package com.lionxcr.real.time.tracker.src

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.*

import com.google.android.gms.location.*
import java.util.*


class TrackerCurrentLocation(private val reactContext: ReactApplicationContext) {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var locationPromise: Promise? = null

    @SuppressLint("MissingPermission")
    fun findCurrentLocation(promise: Promise) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(reactContext)
        mLocationCallback = createLocationRequestCallback()
        locationPromise = promise

        val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest: LocationSettingsRequest = builder.build()

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(reactContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)


        Handler(reactContext.mainLooper).post { fusedLocationProviderClient!!.requestLocationUpdates(locationRequest, mLocationCallback!!, Looper.myLooper()) }
    }

    private fun createLocationRequestCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    if (locationPromise != null){
                        locationPromise!!.reject(Error("Unable to get location"))
                    }
                    return
                }
                val location = locationResult.locations.last()
                val time = Date().time
                val eventData = Arguments.createMap()
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_LAT_KEY,
                        location.latitude)
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_LON_KEY,
                        location.longitude)
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_TIME_KEY,
                        time.toDouble())
                if (locationPromise != null){
                    locationPromise!!.resolve(eventData)
                }
                fusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
            }

        }
    }

}
