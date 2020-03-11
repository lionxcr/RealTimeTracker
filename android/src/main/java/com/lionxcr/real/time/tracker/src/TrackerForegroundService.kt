package com.lionxcr.real.time.tracker.src

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class TrackerForegroundService : Service() {


    private var mAlarmManager: AlarmManager? = null
    private var mLocationBackgroundServicePendingIntent: PendingIntent? = null

    override fun onCreate() {
        super.onCreate()
        mAlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(intent))
        createLocationPendingIntent()
        mAlarmManager!!.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis(),
                LOCATION_UPDATE_INTERVAL.toLong(),
                mLocationBackgroundServicePendingIntent
        )

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        mAlarmManager!!.cancel(mLocationBackgroundServicePendingIntent)
        stopSelf()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(notificationIntent: Intent): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build()
    }

    private fun createLocationPendingIntent() {
        val i = Intent(applicationContext, TrackerBackgroundService::class.java)
        mLocationBackgroundServicePendingIntent = PendingIntent.getService(applicationContext, 1, i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val CHANNEL_ID = "TrackerForegroundService"
        const val NOTIFICATION_ID = 1
        const val LOCATION_EVENT_NAME = "com.lionxcr.real.time.tracker.LOCATION_INFO"
        const val LOCATION_EVENT_DATA_NAME = "LocationData"
        const val LOCATION_UPDATE_INTERVAL = 60000 // 60 seconds
        const val JS_LOCATION_LAT_KEY = "latitude"
        const val JS_LOCATION_LON_KEY = "longitude"
        const val JS_LOCATION_TIME_KEY = "timestamp"
        const val JS_LOCATION_EVENT_NAME = "location_received"
    }
}
