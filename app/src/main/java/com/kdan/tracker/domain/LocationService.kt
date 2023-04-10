package com.kdan.tracker.domain

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
import com.kdan.tracker.database.mark.Mark
import com.kdan.tracker.database.mark.MarkDatabase
import com.kdan.tracker.database.user_data.UserData
import com.kdan.tracker.database.user_data.UserDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var remoteDb: FirebaseFirestore
    private lateinit var localDb: MarkDatabase
    private lateinit var userDataDb: UserDatabase
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
        localDb = MarkDatabase.getDatabase(applicationContext)
        userDataDb = UserDatabase.getDatabase(applicationContext)
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

        GlobalScope.launch {
            userDataDb.dao.upsertUserData(UserData(email = TrackerApp.email, serviceState = "on"))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else stopForeground(true)
        stopSelf()

        GlobalScope.launch {
            userDataDb.dao.upsertUserData(UserData(email = TrackerApp.email, serviceState = "off"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        fun startTracking(applicationContext: Context) {
            Intent(applicationContext, LocationService::class.java).apply {
                action = ACTION_START
                applicationContext.startService(this)
            }
        }

        fun stopTracking(applicationContext: Context) {
            Intent(applicationContext, LocationService::class.java).apply {
                action = ACTION_STOP
                applicationContext.startService(this)
            }
        }
    }
}