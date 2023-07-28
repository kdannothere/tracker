package com.kdan.tracker.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kdan.coredatabase.AppDatabase
import com.kdan.coredatabase.di.AppModule
import com.kdan.coredatabase.mark.MarkRepository
import com.kdan.tracker.TrackerApp
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltWorker
class WorkerSendLocation @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : Worker(appContext, workerParams), CoroutineScope {

    private val repository = MarkRepository(
        dao = AppModule.getMarkDao(
            appDatabase = AppDatabase.getDatabase(appContext)
        )
    )
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private val scope = CoroutineScope(coroutineContext)

    override fun doWork(): Result {
        if (TrackerApp.email.isBlank()) return Result.success()
        val remoteDb = Firebase.firestore
        return try {
            scope.launch(dispatcherIO) {
                val localMarks = repository.getMarks(TrackerApp.email)
                localMarks.forEach { mark ->
                    remoteDb.collection(AppDatabase.tableRemoteMarks)
                        .add(mark)
                        .addOnSuccessListener {
                            scope.launch(dispatcherIO) {
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