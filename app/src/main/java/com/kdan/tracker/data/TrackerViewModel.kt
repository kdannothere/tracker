package com.kdan.tracker.data

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.Utility.setStatusLoading
import com.kdan.tracker.utility.Utility.setStatusTrackerOff

class TrackerViewModel : ViewModel() {

    private val statusState = mutableStateOf(Status.TRACKER_IS_OFF)
    val status get() = statusState.value

    fun startTracker(applicationContext: Context) {
        when (status) {
            Status.TRACKER_IS_OFF -> {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    applicationContext.startService(this)
                    setStatusLoading(statusState)
                }
            }
            else -> {
                stopTracker(applicationContext)
            }
        }
    }

    fun stopTracker(applicationContext: Context) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            applicationContext.startService(this)
            setStatusTrackerOff(statusState)
        }
    }

}