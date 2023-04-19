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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.tracker.MainActivity
import com.kdan.tracker.R
import com.kdan.tracker.TrackerApp
import com.kdan.tracker.domain.LocationService
import com.kdan.tracker.utility.CurrentStatus
import com.kdan.tracker.utility.Status
import com.kdan.tracker.utility.Utility

@Composable
fun FragmentTracker(
    navController: NavHostController,
    applicationContext: Context,
    activity: MainActivity,
    viewModelStoreOwner: ViewModelStoreOwner,
    authViewModel: AuthViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
) {
    TrackerApp.email = authViewModel.getUserEmail()
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
                CurrentStatus.isLoggingOut = true
                LocationService.stopTracking(applicationContext)
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
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                if (Utility.hasLocationPermission(applicationContext)) {
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
private fun ButtonStartStop(
    applicationContext: Context,
) {
    val textOfButton = if (CurrentStatus.status.value == Status.TRACKER_IS_OFF ||
        CurrentStatus.status.value == Status.HAS_NO_PERMISSIONS
    ) {
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

fun runTracker(applicationContext: Context) {
    if (CurrentStatus.status.value == Status.TRACKER_IS_OFF ||
        CurrentStatus.status.value == Status.HAS_NO_PERMISSIONS
    ) {
        LocationService.startTracking(applicationContext)
    } else {
        LocationService.stopTracking(applicationContext)
    }
}