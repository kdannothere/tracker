package com.kdan.tracker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kdan.tracker.database.TrackerDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WorkerSendLocation(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        const val TAG = "WorkerSendLocation"
    }

    override fun doWork(): Result {
        val remoteDb = Firebase.firestore
        val localDb = TrackerDatabase.getDatabase(applicationContext)
        return try {
            val localMarks = localDb.dao.getAllMarks()
            localMarks.forEach { mark ->
                remoteDb.collection("remote_marks")
                    .add(mark)
                    .addOnSuccessListener {
                        GlobalScope.launch {
                            localDb.dao.deleteMark(mark)
                        }
                    }
            }
            Log.d("SHOW", "WorkerSendLocation - Success")
            Result.success()
        } catch (throwable: Throwable) {
            Log.d("SHOW", "WorkerSendLocation - Error")
            Result.failure()
        }
    }
}