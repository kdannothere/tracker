package com.kdan.tracker.screen

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kdan.tracker.MainActivity
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.utility.Utility


@Composable
fun ShowAlertDialog() {
    val context = LocalContext.current
    val activity = context as? MainActivity
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.dialog_title))
        },
        text = {
            Text(stringResource(id = R.string.dialog_text))
        },
        onDismissRequest = { },
        buttons = {
            Button(onClick = {
                TrackerApp.showAlertDialog.value = false
            }) {
                Text(stringResource(id = R.string.dialog_text_button_okay))
            }
            Button(onClick = {
                if (activity != null)
                Utility.requestPermissions(activity)
            }) {
                Text(stringResource(id = R.string.dialog_text_button_give))
            }
        }
    )
}