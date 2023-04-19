package com.kdan.map.utility

import com.google.android.gms.maps.model.LatLng
import com.kdan.coredatabase.markmap.MarkMap
import java.text.DateFormat
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object Utility {

    // to show date and time in the correct way
    val dateTimeFormat: DateFormat =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    // get default time range - from yesterday to now
    fun getInitialTimeRange(): Pair<Long, Long> {
        val now = Date().time.milliseconds.inWholeMilliseconds
        val dayAgo = now - 1.toDuration(DurationUnit.DAYS).inWholeMilliseconds
        return Pair(
            first = dayAgo,
            second = now
        )
    }

    // get sorted marks from earlier to later
    fun getSortedOutMarks(marks: List<MarkMap>): List<MarkMap> {
        val sorted = mutableListOf<MarkMap>()
        marks.forEach { mark ->
            val time = mark.time.toLong()
            when {
                sorted.isEmpty() -> sorted.add(mark)
                sorted.last().time.toLong() > time -> {
                    var currentIndex = sorted.lastIndex
                    while (currentIndex > 0 && sorted[currentIndex].time.toLong() > time) {
                        --currentIndex
                    }
                    sorted.add(currentIndex, mark)
                }

                else -> sorted.add(mark)
            }
        }
        return sorted
    }

    // get connected in pairs points
    fun getConnectedInPairsPoints(points: List<LatLng>): List<Pair<LatLng, LatLng>> {
        val connected = mutableListOf<Pair<LatLng, LatLng>>()
        var lastPoint = points.first()
        for (index in 1..points.lastIndex) {
            connected += Pair(lastPoint, points[index])
            lastPoint = points[index]
        }
        return connected
    }
}