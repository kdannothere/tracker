package com.kdan.tracker.domain

import android.content.Context
import android.content.Intent

object ControlService {

    fun startTracking(applicationContext: Context) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            applicationContext.startService(this)
        }
    }

    fun stopTracking(applicationContext: Context) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            applicationContext.startService(this)
        }
    }
}