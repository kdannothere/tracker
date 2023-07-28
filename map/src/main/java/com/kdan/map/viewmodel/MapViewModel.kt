package com.kdan.map.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.CameraPositionState
import com.kdan.coredatabase.AppDatabase
import com.kdan.coredatabase.markmap.MarkMap
import com.kdan.coredatabase.markmap.MarkMapRepository
import com.kdan.map.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MarkMapRepository,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val remoteDb = Firebase.firestore
    val allMarks = mutableSetOf<MarkMap>()
    val marksInTimeRange = mutableStateOf(listOf<MarkMap>())
    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(
            LatLng(50.0, 22.0), 4f
        )
    )

    var timeFrom = 0L
    var timeTo = 0L

    val textDateFrom = mutableStateOf("")
    val textTimeFrom = mutableStateOf("")
    val textDateTo = mutableStateOf("")
    val textTimeTo = mutableStateOf("")

    var fromDay = 0
    var fromMonth = 0
    var fromYear = 0
    var fromHour = 0
    var fromMinute = 0

    var toDay = 0
    var toMonth = 0
    var toYear = 0
    var toHour = 0
    var toMinute = 0


    init {
        Utility.getInitialTimeRange().apply {
            timeFrom = first
            timeTo = second
        }
    }

    fun updateAllMarks(email: String) {
        viewModelScope.launch(dispatcherIO) {
            allMarks.addAll(
                elements = repository.getMarks(email)
            )
            if (allMarks.isEmpty()) {
                loadMarksFromCloud(email)
            } else updateMarksInTimeRange()

        }
    }

    fun updateMarksInTimeRange() {
        viewModelScope.launch(dispatcherIO) {
            val newList = mutableListOf<MarkMap>()

            val range = if (timeFrom > timeTo) {
                timeTo..timeFrom
            } else timeFrom..timeTo

            allMarks.forEach { mark ->
                if (mark.time.toLong() in range) {
                    newList.add(mark)
                }
            }
            marksInTimeRange.value = Utility.getSortedOutMarks(newList)
        }
    }

    fun loadMarksFromCloud(email: String) {
        viewModelScope.launch(dispatcherIO) {
            if (email.isBlank()) return@launch
            remoteDb.collection(AppDatabase.tableRemoteMarks)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { marks ->
                    for (mark in marks) {
                        val newMark = MarkMap(
                            time = mark.get("time").toString(),
                            email = email,
                            latitude = mark.get("latitude").toString(),
                            longitude = mark.get("longitude").toString()
                        )
                        viewModelScope.launch(dispatcherIO) {
                            repository.upsertMark(newMark)
                        }

                    }
                }
        }
        updateAllMarks(email)
    }

    fun clearLocalMarks() {
        viewModelScope.launch(dispatcherIO) {
            allMarks.forEach { mark ->
                repository.deleteMark(mark)
            }
            allMarks.clear()
        }
    }
}