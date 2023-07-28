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
import com.kdan.coredatabase.AppDatabase
import com.kdan.coredatabase.mark.Mark
import com.kdan.coredatabase.mark.MarkRepository
import com.kdan.coredatabase.user.User
import com.kdan.coredatabase.user.UserRepository
import com.kdan.tracker.BuildConfig
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.utility.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService(private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO) : Service() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var markRepository: MarkRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var remoteDb: FirebaseFirestore
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
                remoteDb.collection(AppDatabase.tableRemoteMarks)
                    .add(mark)
                    .addOnFailureListener {
                        serviceScope.launch(dispatcherIO) {
                            markRepository.upsertMark(mark)
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

        serviceScope.launch(dispatcherIO) {
            userRepository.upsertUser(
                User(
                    email = TrackerApp.email,
                    serviceState = "on"
                )
            )
        }
    }

    private fun stop() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else stopForeground(true)
        stopSelf()

        serviceScope.launch(dispatcherIO) {
            if (TrackerApp.isLoggingOut) {
                TrackerApp.email = ""
                TrackerApp.isLoggingOut = false
            }
            userRepository.upsertUser(
                User(
                    email = TrackerApp.email,
                    serviceState = "off"
                )
            )
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
            TrackerApp.setNewStatus(Status.LOADING)
            Intent(applicationContext, LocationService::class.java).apply {
                action = ACTION_START
                applicationContext.startService(this)
            }
        }

        fun stopTracking(applicationContext: Context) {
            TrackerApp.setNewStatus(Status.TRACKER_IS_OFF)
            Intent(applicationContext, LocationService::class.java).apply {
                action = ACTION_STOP
                applicationContext.startService(this)
            }
        }
    }
}