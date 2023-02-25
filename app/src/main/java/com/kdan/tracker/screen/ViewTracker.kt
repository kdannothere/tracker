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
import com.kdan.authorization.AuthViewModel
import com.kdan.authorization.RoutesAuth
import com.kdan.tracker.R
import com.kdan.tracker.data.TrackerViewModel
import com.kdan.tracker.data.Status

@Composable
fun ViewTracker(
    navController: NavHostController,
    applicationContext: Context,
    trackerViewModel: TrackerViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top) {
            TextButton(onClick = {
                trackerViewModel.stopTracker(applicationContext)
                authViewModel.logOut()
                navController.navigate(RoutesAuth.ViewSignIn)
            }) {
                Text(text = stringResource(id = R.string.button_log_out),
                    fontSize = 20.sp)
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShowStatusText(trackerViewModel.status)
            Spacer(modifier = Modifier.height(30.dp))
            ButtonStartStop(applicationContext)
        }
    }
}

@Composable
private fun ShowStatusText(
    status: Status,
) {
    val textToShow = when (status) {
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
        Status.ERROR -> {
            stringResource(id = R.string.tracker_cant_collect)
        }
    }
    Text(text = textToShow)
}

@Composable
private fun ButtonStartStop(
    applicationContext: Context,
    viewModel: TrackerViewModel = viewModel(),
) {
    val textOfButton = if (viewModel.status == Status.TRACKER_IS_OFF) {
        stringResource(id = R.string.button_start)
    } else {
        stringResource(id = R.string.button_stop)
    }
    Button(
        onClick = {
            viewModel.startTracker(applicationContext)
        }) {
        Text(text = textOfButton,
            fontSize = 20.sp)
    }
}