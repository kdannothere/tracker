package com.kdan.tracker

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.kdan.coredatabase.user.User
import com.kdan.coredatabase.user.UserRepository
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TrackerApp : Application(),
    Configuration.Provider {

    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private lateinit var locationManager: LocationManager
    private lateinit var requestSendLocation: WorkRequest
    private lateinit var handler: Handler
    private val delay = 2000L
    private var tempThread: Runnable? = null

    companion object {
        const val CHANNEL_ID = "tracker"
        const val CHANNEL_NAME = "Tracker"
        val showAlertDialog = mutableStateOf(false)
        var email = ""
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            var user: User? = repository.getUser()
            if (user == null) {
                repository.upsertUser(User())
                user = repository.getUser()
            }
            if (user != null) {
                email = user.email
                if (user.serviceState == "on" && user.email != "") {
                    CurrentStatus.setNewStatus(Status.LOADING)
                    LocationService.startTracking(applicationContext)
                }
            }
        }
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestSendLocation = PeriodicWorkRequestBuilder<WorkerSendLocation>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()
        WorkManager.getInstance(applicationContext).enqueue(requestSendLocation)
        handler = Handler(Looper.getMainLooper())
        checker()
    }

    private fun checker() {
        handler.postDelayed(Runnable {
            handler.postDelayed(tempThread!!, delay)
            when (CurrentStatus.status.value) {
                Status.LOADING -> {
                    if (!Utility.checkGps(locationManager)) {
                        CurrentStatus.setNewStatus(Status.GPS_IS_OFF)
                    }
                }

                Status.GPS_IS_OFF -> {
                    if (Utility.checkGps(locationManager)) {
                        CurrentStatus.setNewStatus(Status.LOADING)
                    }
                }

                Status.HAS_NO_PERMISSIONS -> {
                    if (showAlertDialog.value) return@Runnable
                    if (!Utility.hasLocationPermission(applicationContext)) {
                        showAlertDialog.value = true
                        LocationService.stopTracking(applicationContext)
                    } else {
                        CurrentStatus.setNewStatus(Status.LOADING)
                        LocationService.startTracking(applicationContext)
                    }
                }

                else -> {
                    return@Runnable
                }
            }
        }.also {
            tempThread = it
        }, delay)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}