package com.kdan.tracker.domain

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import com.kdan.tracker.BuildConfig
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val period: Long = BuildConfig.PERIOD,
    private val sensitivity: Float = BuildConfig.SENSITIVITY,
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {

            if (!Utility.hasLocationPermission(context)) {
                CurrentStatus.setNewStatus(Status.HAS_NO_PERMISSIONS)
                throw LocationClient.LocationException("Missing location permission")
            }
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!Utility.checkGps(locationManager)) {
                CurrentStatus.setNewStatus(Status.GPS_IS_OFF)
                throw LocationClient.LocationException("GPS is disabled")
            }
            val request = createRequest()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    val location = result.locations.lastOrNull()
                    if (location == null) {
                        LocationService.stopTracking(context)
                    } else launch { send(location) }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun createRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, period).apply {
            setMinUpdateDistanceMeters(sensitivity)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    }
}