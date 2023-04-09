package com.kdan.tracker

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TrackerApp: Application() {

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

    override fun onCreate() {
        super.onCreate()
        val sharedPref = getSharedPreferences("trackerPref", MODE_PRIVATE)
        email = sharedPref.getString("email", null).toString()
        val status = sharedPref.getString("status", null).toString()
        if (status == "on") {
            CurrentStatus.setNewStatus(Status.LOADING)
            LocationService.startTracking(applicationContext)
        }
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
                    val locationManager =
                        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    if (!Utility.checkGps(locationManager)) {
                        CurrentStatus.setNewStatus(Status.GPS_IS_OFF)
                    }
                }
                Status.GPS_IS_OFF -> {
                    val locationManager =
                        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

}