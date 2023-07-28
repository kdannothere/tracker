package com.kdan.map.screen.elements

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
import com.kdan.map.viewmodel.MapViewModel
import java.util.Calendar
import java.util.Date

// be careful with date that you save and date that you show
// first month has index 0, not 1
@Composable
fun DateTimePickers(
    mapViewModel: MapViewModel,
) {
    val currentYear: Int
    val currentMonth: Int
    val currentDay: Int

    val currentHour: Int
    val currentMinute: Int

    val calendar = Calendar.getInstance()
    val fakeCalendar = Calendar.getInstance() // to set and save date and time

    calendar.apply {
        currentYear = get(Calendar.YEAR)
        currentMonth = get(Calendar.MONTH)
        currentDay = get(Calendar.DAY_OF_MONTH)

        currentHour = get(Calendar.HOUR_OF_DAY)
        currentMinute = get(Calendar.MINUTE)
    }

    mapViewModel.apply {
        if (textDateFrom.value.isBlank()) {

            fromDay = currentDay - 1
            fromMonth = currentMonth
            fromYear = currentYear
            fromHour = currentHour
            fromMinute = currentMinute

            toDay = currentDay
            toMonth = currentMonth
            toYear = currentYear
            toHour = currentHour
            toMinute = currentMinute

            textDateFrom.value = "${currentDay - 1}/${currentMonth + 1}/$currentYear"
            textTimeFrom.value = "${currentHour}:${currentMinute}"
            textDateTo.value = "${currentDay}/${currentMonth + 1}/$currentYear"
            textTimeTo.value = "${currentHour}:${currentMinute}"
        }
    }

    calendar.time = Date()

    val datePickerDialogFrom = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, newYear: Int, newMonth: Int, newDay: Int ->
            mapViewModel.apply {
                textDateFrom.value = "$newDay/${newMonth + 1}/$newYear"
                fromYear = newYear
                fromMonth = newMonth
                fromDay = newDay
                fakeCalendar.apply {
                    set(
                        newYear, newMonth, newDay,
                        fromHour, fromMinute,
                    )
                }
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
                fromHour = newHour
                fromMinute = newMinute
                fakeCalendar.set(
                    fromYear, fromMonth, fromDay,
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
            mapViewModel.apply {
                textDateTo.value = "$newDay/${newMonth + 1}/$newYear"
                toYear = newYear
                toMonth = newMonth
                toDay = newDay
                fakeCalendar.set(newYear, newMonth, newDay, toHour, toMinute)
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
                toHour = newHour
                toMinute = newMinute
                fakeCalendar.set(
                    toYear, toMonth, toDay,
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