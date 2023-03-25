package com.kdan.tracker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kdan.tracker.domain.LocationService

class WorkerGetLocation(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        const val TAG = "WorkerGetLocation"
    }

    override fun doWork(): Result {
        return try {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                applicationContext.startService(this)
            }
            Log.d("SHOW", "WorkerGetLocation - Success")
            Result.success()
        } catch (throwable: Throwable) {
            Log.d("SHOW", "WorkerGetLocation - Error")
            Result.failure()
        }
    }
}