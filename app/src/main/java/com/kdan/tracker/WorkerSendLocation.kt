package com.kdan.tracker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kdan.coredatabase.AppDatabase
import com.kdan.coredatabase.mark.MarkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltWorker
class WorkerSendLocation @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

    private val repository by lazy {
        MarkRepository(dao = AppDatabase.getDatabase(appContext).getMarkDao())
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun doWork(): Result {
        if (TrackerApp.email.isBlank()) return Result.success()
        val remoteDb = Firebase.firestore
        return try {
            GlobalScope.launch {
                val localMarks = repository.getMarks(TrackerApp.email)
                localMarks.forEach { mark ->
                    remoteDb.collection(AppDatabase.tableRemoteMarks)
                        .add(mark)
                        .addOnSuccessListener {
                            GlobalScope.launch {
                                repository.deleteMark(mark)
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