package com.kdan.map.screen.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.kdan.coredatabase.markmap.MarkMap
import com.kdan.map.utility.Utility

@Composable
fun ShowOnMap(marksInTimeRange: MutableState<List<MarkMap>>) {
    when (marksInTimeRange.value.size) {

        0 -> return

        1 -> {
            return Marker(
                state = MarkerState(
                    position = LatLng(
                        marksInTimeRange.value.first().latitude.toDouble(),
                        marksInTimeRange.value.first().longitude.toDouble()
                    )
                ),
                title = Utility.dateTimeFormat.format(marksInTimeRange.value.first().time.toLong())
            )
        }

        2 -> {
            return ShowStartEndMarkers(marksInTimeRange.value).also {
                val start: LatLng
                val end: LatLng
                marksInTimeRange.value.run {
                    start = LatLng(
                        first().latitude.toDouble(),
                        first().longitude.toDouble()
                    )
                    end = LatLng(
                        last().latitude.toDouble(),
                        last().longitude.toDouble()
                    )
                }
                Polyline(
                    points = listOf(start, end),
                    jointType = JointType.ROUND
                )
            }
        }

        else -> {
            val points = mutableListOf<LatLng>()
            marksInTimeRange.value.forEach { mark ->
                val latitude = mark.latitude.toDouble()
                val longitude = mark.longitude.toDouble()
                val point = LatLng(latitude, longitude)
                points.add(point)
            }

            Utility.getConnectedInPairsPoints(points).forEach { pair ->
                Polyline(
                    points = listOf(pair.first, pair.second),
                    jointType = JointType.ROUND
                )
            }
            ShowStartEndMarkers(marksInTimeRange.value)
        }
    }
}

@Composable
private fun ShowStartEndMarkers(marksInTimeRange: List<MarkMap>) {
    val start: LatLng
    val end: LatLng
    marksInTimeRange.run {
        start = LatLng(
            first().latitude.toDouble(),
            first().longitude.toDouble()
        )
        end = LatLng(
            last().latitude.toDouble(),
            last().longitude.toDouble()
        )
    }
    repeat(2) { index ->
        Marker(
            state = MarkerState(
                position = if (index == 0) start else end
            ),
            snippet = if (index == 0) "START" else "END",
            title = if (index == 0) {
                Utility.dateTimeFormat.format(marksInTimeRange.first().time.toLong())
            } else Utility.dateTimeFormat.format(marksInTimeRange.last().time.toLong()),
        )
    }
}