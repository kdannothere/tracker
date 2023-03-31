package com.kdan.tracker.utility

import androidx.compose.runtime.mutableStateOf

object CurrentStatus {
    val status = mutableStateOf(Status.TRACKER_IS_OFF)

    fun setNewStatus(newStatus: Status) {
        status.value = newStatus
    }
}