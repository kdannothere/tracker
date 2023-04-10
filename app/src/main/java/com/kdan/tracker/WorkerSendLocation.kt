package com.kdan.tracker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kdan.tracker.database.mark.MarkDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WorkerSendLocation(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val remoteDb = Firebase.firestore
        val localDb = MarkDatabase.getDatabase(applicationContext)
        return try {
            GlobalScope.launch {
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
            }
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}