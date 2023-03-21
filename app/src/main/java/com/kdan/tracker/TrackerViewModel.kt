package com.kdan.tracker

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdan.tracker.database.MarkDao
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility
import kotlinx.coroutines.launch

class TrackerViewModel : ViewModel() {

    private val _status = mutableStateOf(Status.TRACKER_IS_OFF)
    val status get() = _status.value

    fun startTracker(applicationContext: Context) {
        when (status) {
            Status.TRACKER_IS_OFF -> {
                Utility.setStatusLoading(_status)
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    applicationContext.startService(this)
                }
            }
            else -> {
                Utility.setStatusTrackerOff(_status)
                stopTracker(applicationContext)
            }
        }
    }

    fun stopTracker(applicationContext: Context) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            applicationContext.startService(this)
        }
    }
}