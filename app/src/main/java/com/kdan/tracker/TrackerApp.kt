package com.kdan.tracker

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class TrackerApp: Application() {

    private lateinit var requestSendLocation: WorkRequest

    companion object {
        const val CHANNEL_ID = "tracker"
        const val CHANNEL_NAME = "Tracker"
    }

    override fun onCreate() {
        super.onCreate()
        requestSendLocation = PeriodicWorkRequestBuilder<WorkerSendLocation>(
            BuildConfig.PERIOD, TimeUnit.MILLISECONDS
        ).build()
        WorkManager.getInstance(applicationContext).enqueue(requestSendLocation)
    }

}