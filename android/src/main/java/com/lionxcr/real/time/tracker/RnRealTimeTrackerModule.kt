package com.lionxcr.real.time.tracker


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*

import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.Gson
import com.lionxcr.real.time.tracker.src.*

import java.util.HashMap

class  RnRealTimeTrackerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), EventReceiver, EventSender {
    private val mForegroundServiceIntent: Intent = Intent(reactContext, TrackerForegroundService::class.java)
    private var mEventReceiver: BroadcastReceiver? = null
    private val mGson: Gson = Gson()
    private val currentTracker: TrackerCurrentLocation = TrackerCurrentLocation(reactContext)

    init {
        createReceiver()
        registerReceiver()
    }

    override fun getName(): String {
        return "RnRealTimeTracker"
    }

    @ReactMethod
    fun startBackgroundLocation() {
        ContextCompat.startForegroundService(reactContext, mForegroundServiceIntent)
    }

    @ReactMethod
    fun stopBackgroundLocation() {
       reactContext.stopService(mForegroundServiceIntent)
    }

    @ReactMethod
    fun getCurrentLocationForUser(promise: Promise) {
        currentTracker.findCurrentLocation(promise)
    }

    override fun getConstants(): Map<String, Any>? {
        val constants = HashMap<String, Any>()
        constants[CONST_RN_LOCATION_EVENT] = TrackerForegroundService.JS_LOCATION_EVENT_NAME
        constants[CONST_RN_LOCATION_LAT] = TrackerForegroundService.JS_LOCATION_LAT_KEY
        constants[CONST_RN_LOCATION_LON] = TrackerForegroundService.JS_LOCATION_LON_KEY
        constants[CONST_RN_LOCATION_TIME] = TrackerForegroundService.JS_LOCATION_TIME_KEY
        return constants
    }


    override fun createReceiver() {
        mEventReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val (latitude, longitude, timestamp) = mGson.fromJson(
                        intent.getStringExtra(TrackerForegroundService.LOCATION_EVENT_DATA_NAME), LocationCoordinates::class.java)
                val eventData = Arguments.createMap()
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_LAT_KEY,
                        latitude)
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_LON_KEY,
                        longitude)
                eventData.putDouble(
                        TrackerForegroundService.JS_LOCATION_TIME_KEY,
                        timestamp.toDouble())
                sendEvents(reactApplicationContext,
                        TrackerForegroundService.JS_LOCATION_EVENT_NAME, eventData)
            }
        }
    }

    override fun registerReceiver() {
        val eventFilter = IntentFilter()
        eventFilter.addAction(TrackerForegroundService.LOCATION_EVENT_NAME)
        this.reactContext.registerReceiver(mEventReceiver, eventFilter)
    }

    override fun sendEvents(reactContext: ReactContext, eventName: String, params: WritableMap) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
    }

    companion object {
        private const val CONST_RN_LOCATION_EVENT = "RN_LOCATION_EVENT"
        private const val CONST_RN_CURRENT_LOCATION_EVENT = "RN_CURRENT_LOCATION_EVENT"
        private const val CONST_RN_LOCATION_LAT = "RN_LOCATION_LAT"
        private const val CONST_RN_LOCATION_LON = "RN_LOCATION_LON"
        private const val CONST_RN_LOCATION_TIME = "RN_LOCATION_TIME"
    }
}
