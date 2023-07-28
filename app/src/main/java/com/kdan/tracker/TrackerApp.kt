package com.kdan.tracker

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.kdan.coredatabase.user.User
import com.kdan.coredatabase.user.UserRepository
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility
import com.kdan.tracker.workmanager.WorkerSendLocation
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class TrackerApp(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
) : Application(),
    Configuration.Provider, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private lateinit var locationManager: LocationManager
    private lateinit var requestSendLocation: WorkRequest
    private lateinit var handler: Handler


    companion object {
        lateinit var instance: TrackerApp
            private set

        const val CHANNEL_ID = "tracker"
        const val CHANNEL_NAME = "Tracker"
        val showAlertDialog = mutableStateOf(false)
        var email = ""

        private val scope = CoroutineScope(instance.coroutineContext)
        private val status = MutableStateFlow(Status.TRACKER_IS_OFF)
        var isLoggingOut = false

        fun setNewStatus(newStatus: Status) {
            scope.launch(Dispatchers.IO) {
                status.emit(newStatus)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        launch {
            var user: User? = repository.getUser()
            if (user == null) {
                repository.upsertUser(User())
                user = repository.getUser()
            }
            if (user != null) {
                email = user.email
                if (user.serviceState == "on" && user.email != "") {
                    setNewStatus(Status.LOADING)
                    LocationService.startTracking(applicationContext)
                }
            }
        }
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestSendLocation = PeriodicWorkRequestBuilder<WorkerSendLocation>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()
        WorkManager.getInstance(applicationContext).enqueue(requestSendLocation)
        handler = Handler(Looper.getMainLooper())
        checker()
    }

    override fun onTerminate() {
        super.onTerminate()
        job.cancel()
    }

    private fun checker() {
        status.onEach { status: Status ->
            when (status) {
                Status.LOADING -> {
                    if (!Utility.checkGps(locationManager)) {
                        setNewStatus(Status.GPS_IS_OFF)
                    }
                }

                Status.GPS_IS_OFF -> {
                    if (Utility.checkGps(locationManager)) {
                        setNewStatus(Status.LOADING)
                    }
                }

                Status.HAS_NO_PERMISSIONS -> {
                    if (!Utility.hasLocationPermission(applicationContext)) {
                        showAlertDialog.value = true
                        LocationService.stopTracking(applicationContext)
                    } else {
                        setNewStatus(Status.LOADING)
                        LocationService.startTracking(applicationContext)
                    }
                }

                else -> {} // not needed
            }

        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

/*

 ********************
 * IDEAS TO IMPROVE *
 ********************

 add parent control option to get and show user location in real time
 (now you need to go to settings and press "Load marks from cloud"
 to see the last saved location)

 add error messages

 add options to settings to save last map scale
 or to set map scale based on user location

 change start-end markers images

 add markers and info for saved locations

 ***************
 * WHAT TO FIX *
 ***************

 numbers of date and time

 sometimes it has crash on launch, it only shows [hilt]

*/