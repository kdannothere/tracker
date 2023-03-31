package com.kdan.tracker.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.tracker.MainActivity
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.domain.ControlService
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun FragmentTracker(
    navController: NavHostController,
    applicationContext: Context,
    activity: MainActivity,
    authViewModel: AuthViewModel = viewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        if (TrackerApp.showAlertDialog.value) ShowAlertDialog(activity)
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            TextButton(onClick = {
                ControlService.stopTracking(applicationContext)
                authViewModel.logOut()
                navController.navigate(RoutesAuth.fragmentSignIn)
            }) {
                Text(
                    text = stringResource(id = R.string.button_log_out),
                    fontSize = 20.sp
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShowStatusText()
            Spacer(modifier = Modifier.height(30.dp))
            ButtonStartStop(applicationContext)
        }
    }
}

@Composable
private fun ShowStatusText() {
    val textToShow = when (CurrentStatus.status.value) {
        Status.TRACKER_IS_OFF -> {
            stringResource(id = R.string.state_tracker_off)
        }
        Status.HAS_NO_PERMISSIONS -> {
            stringResource(id = R.string.state_no_permissions)
        }
        Status.GPS_IS_OFF -> {
            stringResource(id = R.string.state_gps_off)
        }
        Status.LOADING -> {
            stringResource(id = R.string.state_loading)
        }
    }
    Text(text = textToShow)
}

@Composable
private fun ButtonStartStop(
    applicationContext: Context,
) {
    val textOfButton = if (CurrentStatus.status.value == Status.TRACKER_IS_OFF ||
        CurrentStatus.status.value == Status.HAS_NO_PERMISSIONS) {
        stringResource(id = R.string.button_start)
    } else {
        stringResource(id = R.string.button_stop)
    }
    Button(
        onClick = {
            runTracker(applicationContext)
        }) {
        Text(
            text = textOfButton,
            fontSize = 20.sp
        )
    }
}

fun runTracker(context: Context) {
    if (CurrentStatus.status.value == Status.TRACKER_IS_OFF ||
        CurrentStatus.status.value == Status.HAS_NO_PERMISSIONS
    ) {
        GlobalScope.launch {
            CurrentStatus.setNewStatus(Status.LOADING)
            ControlService.startTracking(context)
        }
    } else {
        GlobalScope.launch {
            CurrentStatus.setNewStatus(Status.TRACKER_IS_OFF)
            ControlService.stopTracking(context)
        }
    }
}