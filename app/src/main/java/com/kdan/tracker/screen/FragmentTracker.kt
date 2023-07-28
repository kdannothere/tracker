package com.kdan.tracker.screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility

@Composable
fun FragmentTracker(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    LaunchedEffect(Unit) {
        TrackerApp.email = authViewModel.getUserEmail()
    }
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        if (TrackerApp.showAlertDialog.value) ShowAlertDialog()
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            TextButton(onClick = {
                CurrentStatus.isLoggingOut = true
                LocationService.stopTracking(context)
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
            ButtonStartStop()
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                if (Utility.hasLocationPermission(context)) {
                    navController.navigate(RoutesMap.fragmentMap)
                } else TrackerApp.showAlertDialog.value = true
            }) {
                Text(
                    text = stringResource(id = R.string.button_open_map),
                    fontSize = 20.sp
                )
            }
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
private fun ButtonStartStop() {
    val context = LocalContext.current
    val textOfButton = if (CurrentStatus.status.value == Status.TRACKER_IS_OFF ||
        CurrentStatus.status.value == Status.HAS_NO_PERMISSIONS
    ) {
        stringResource(id = R.string.button_start)
    } else {
        stringResource(id = R.string.button_stop)
    }
    Button(
        onClick = {
            runTracker(context)
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
        LocationService.startTracking(context)
    } else {
        LocationService.stopTracking(context)
    }
}