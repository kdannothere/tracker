package com.kdan.map.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.kdan.map.viewmodel.MapViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun DateTimePickers(
    viewModelStoreOwner: ViewModelStoreOwner,
    mapViewModel: MapViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
) {
    val currentYear: Int
    val currentMonth: Int
    val currentDay: Int

    val currentHour: Int
    val currentMinute: Int

    val calendar = Calendar.getInstance()
    val fakeCalendar = Calendar.getInstance()

    currentYear = calendar.get(Calendar.YEAR)
    currentMonth = calendar.get(Calendar.MONTH)
    currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    currentMinute = calendar.get(Calendar.MINUTE)

    mapViewModel.run {
        if (textDateFrom.value.isBlank()) {

            textDateFrom.value = "${currentDay - 1}/${currentMonth + 1}/$currentYear"
            textTimeFrom.value = "${currentHour}:${currentMinute}"
            textDateTo.value = "${currentDay}/${currentMonth + 1}/$currentYear"
            textTimeTo.value = "${currentHour}:${currentMinute}"

            dateFromDay = currentDay - 1
            dateFromMonth = currentMonth + 1
            dateFromYear = currentYear
            timeFromHour = currentHour
            timeFromMinute = currentMinute

            dateToDay = currentDay
            dateToMonth = currentMonth + 1
            dateToYear = currentYear
            timeToHour = currentHour
            timeToMinute = currentMinute
        }
    }

    calendar.time = Date()

    val datePickerDialogFrom = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, newYear: Int, newMonth: Int, newDay: Int ->
            mapViewModel.run {
                textDateFrom.value = "$newDay/${newMonth + 1}/$newYear"
                fakeCalendar.set(
                    newYear, newMonth + 1, newDay,
                    timeFromHour, timeFromMinute,
                )
                timeFrom = fakeCalendar.timeInMillis
                updateMarksInTimeRange()
            }
        }, currentYear, currentMonth, currentDay
    )
    val timePickerDialogFrom = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, newHour: Int, newMinute: Int ->
            mapViewModel.run {
                textTimeFrom.value = "$newHour:$newMinute"
                fakeCalendar.set(
                    dateFromYear, dateFromMonth + 1, dateFromDay,
                    newHour, newMinute,
                )
                timeFrom = fakeCalendar.timeInMillis
                updateMarksInTimeRange()
            }
        }, currentHour, currentMinute, true
    )
    val datePickerDialogTo = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, newYear: Int, newMonth: Int, newDay: Int ->
            mapViewModel.run {
                textDateTo.value = "$newDay/${newMonth + 1}/$newYear"
                fakeCalendar.set(newYear, newMonth + 1, newDay, timeToHour, timeToMinute)
                timeTo = fakeCalendar.timeInMillis
                updateMarksInTimeRange()
            }
        }, currentYear, currentMonth, currentDay
    )
    val timePickerDialogTo = TimePickerDialog(
        LocalContext.current,
        { _: TimePicker, newHour: Int, newMinute: Int ->
            mapViewModel.run {
                textTimeTo.value = "$newHour:$newMinute"
                fakeCalendar.set(
                    dateToYear, dateToMonth + 1, dateToDay,
                    newHour, newMinute,
                )
                timeTo = fakeCalendar.timeInMillis
                updateMarksInTimeRange()
            }
        }, currentHour, currentMinute, true
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current period:",
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "From" + "\n" + mapViewModel.textDateFrom.value +
                    "\n" + mapViewModel.textTimeFrom.value,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            datePickerDialogFrom.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
            Text(text = "Choose date", color = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            timePickerDialogFrom.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
            Text(text = "Choose time", color = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "To" + "\n" + mapViewModel.textDateTo.value +
                    "\n" + mapViewModel.textTimeTo.value,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            datePickerDialogTo.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
            Text(text = "Choose date", color = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            timePickerDialogTo.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
            Text(text = "Choose time", color = Color.White)
        }
    }
}