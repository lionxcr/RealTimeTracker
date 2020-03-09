package com.lionxcr.real.time.tracker.src

import android.annotation.SuppressLint
import android.location.Location
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext

import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*


class TrackerCurrentLocation(private val reactContext: ReactApplicationContext): EventSender {

    @SuppressLint("MissingPermission")
    fun findCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(reactContext)
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location: Location ->
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
