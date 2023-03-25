package com.kdan.tracker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.Status
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TrackerViewModel : ViewModel() {

    private val _status = mutableStateOf(Status.TRACKER_IS_OFF)
    val status get() = _status.value
    private val requestGetLocation: WorkRequest = if (BuildConfig.BUILD_TYPE == "debug") {
        OneTimeWorkRequestBuilder<WorkerGetLocation>()
            .build()
    } else {
        PeriodicWorkRequestBuilder<WorkerGetLocation>(BuildConfig.PERIOD, TimeUnit.MILLISECONDS)
            .build()
    }

    fun startTracker(applicationContext: Context) {
        when (status) {
            Status.TRACKER_IS_OFF -> {
                setStatusLoading()
                viewModelScope.launch {
                    WorkManager.getInstance(applicationContext)
                        .enqueue(requestGetLocation)
                }

            }
            else -> {
                setStatusTrackerOff()
                viewModelScope.launch {
                    stopTracker(applicationContext)
                }
            }
        }
    }

    fun stopTracker(applicationContext: Context) {
        WorkManager.getInstance(applicationContext)
            .cancelAllWorkByTag(WorkerGetLocation.TAG)
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            applicationContext.startService(this)
        }
    }

    private fun setStatusLoading() = run { _status.value = Status.LOADING }

    private fun setStatusTrackerOff() = run { _status.value = Status.TRACKER_IS_OFF }
}