package com.kdan.map.utility

import java.text.DateFormat
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object Utility {

    val dateTimeFormat: DateFormat =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    fun getInitialTimeRange(): Pair<Long, Long> {
        val now = Date().time.milliseconds
        val dayAgo = (now.inWholeDays - 1).toDuration(DurationUnit.DAYS)
        return Pair(
            first = dayAgo.inWholeMilliseconds,
            second = now.inWholeMilliseconds
        )
    }
}