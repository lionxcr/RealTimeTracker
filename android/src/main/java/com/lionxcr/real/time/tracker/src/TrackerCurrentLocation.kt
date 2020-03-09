package com.lionxcr.real.time.tracker.src

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext

import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.android.gms.location.*
import java.util.*


class TrackerCurrentLocation(private val reactContext: ReactApplicationContext): EventSender {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun findCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(reactContext)
        mLocationCallback = createLocationRequestCallback()

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
                    return
                }
                for (location in locationResult.locations) {
                    val time = Date().time
                    val eventData = Arguments.createMap()
                    eventData.putDouble(
                            TrackerForegroundService.JS_LOCATION_LAT_KEY,
                            location.latitude)
                    eventData.putDouble(
                            TrackerForegroundService.JS_LOCATION_LON_KEY,
                            location.latitude)
                    eventData.putDouble(
                            TrackerForegroundService.JS_LOCATION_TIME_KEY,
                            time.toDouble())
                    sendEvents(reactContext, JS_CURRENT_LOCATION_EVENT_NAME, eventData)

                }

                fusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
            }

        }
    }

    override fun sendEvents(reactContext: ReactContext, eventName: String, params: WritableMap) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
    }

    companion object {
        const val JS_CURRENT_LOCATION_EVENT_NAME = "current_location"
    }

}
