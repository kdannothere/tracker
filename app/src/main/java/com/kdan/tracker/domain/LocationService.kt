package com.kdan.tracker.domain

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kdan.tracker.BuildConfig
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.database.Mark
import com.kdan.tracker.database.TrackerDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var remoteDb: FirebaseFirestore
    private lateinit var localDb: TrackerDatabase
    private lateinit var locationClient: LocationClient


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        remoteDb = Firebase.firestore
        localDb = TrackerDatabase.getDatabase(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                start()
            }
            ACTION_STOP -> stop()
        }
        return START_NOT_STICKY
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun start() {
        val notification = NotificationCompat.Builder(this, TrackerApp.CHANNEL_ID)
            .setContentTitle("Tracker is on")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(interval = BuildConfig.PERIOD)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val mark = Mark(
                    time = location.time.toString(),
                    email = TrackerApp.email,
                    latitude = location.latitude.toString(),
                    longitude = location.longitude.toString(),
                    //dateAndTime = Utility.dateTimeFormat.format(location.time)
                )
                remoteDb.collection("remote_marks")
                    .add(mark)
                    .addOnFailureListener {
                        GlobalScope.launch {
                            localDb.dao.upsertMark(mark)
                        }
                    }
            }
            .launchIn(serviceScope)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TrackerApp.CHANNEL_ID,
                TrackerApp.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(1, notification.build())

        val sharedPref = applicationContext.getSharedPreferences(
            "trackerPref", Application.MODE_PRIVATE
        )
        sharedPref.edit().apply {
            putString("status", "on")
            apply()
        }
    }

    private fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else stopForeground(true)
        stopSelf()

        val sharedPref = applicationContext.getSharedPreferences(
            "trackerPref", Application.MODE_PRIVATE
        )
        sharedPref.edit().apply {
            putString("status", "off")
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}