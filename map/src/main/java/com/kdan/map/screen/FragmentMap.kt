package com.kdan.map.screen

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.coredatabase.markmap.MarkMap
import com.kdan.map.navigation.RoutesMap
import com.kdan.map.utility.Utility
import com.kdan.map.viewmodel.MapViewModel

@Composable
fun FragmentMap(
    navController: NavHostController,
    context: Context,
    routeToTracker: String,
    viewModelStoreOwner: ViewModelStoreOwner,
    authViewModel: AuthViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
    mapViewModel: MapViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = mapViewModel.cameraPositionState,
                onMapLoaded = {
                    mapViewModel.run {
                        if (allMarks.isEmpty()) {
                            updateAllMarks(email = authViewModel.getUserEmail())
                        }
                    }
                }
            ) {
                ShowMarkers(marksInTimeRange = mapViewModel.marksInTimeRange)
            }
            Column(modifier = Modifier.align(alignment = Alignment.TopEnd)) {
                FloatingActionButton(
                    modifier = Modifier.align(alignment = Alignment.End),
                    onClick = {
                        navController.navigate(RoutesMap.fragmentSettings)
                    }
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                FloatingActionButton(
                    modifier = Modifier.align(alignment = Alignment.End),
                    onClick = {
                        navController.navigate(routeToTracker)
                    }
                ) {
                    Text(
                        text = "Tracker",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ShowMarkers(marksInTimeRange: MutableState<List<MarkMap>>) {
    marksInTimeRange.value.forEach { mark ->
        Marker(
            state = MarkerState(
                position = LatLng(mark.latitude.toDouble(), mark.longitude.toDouble())
            ),
            title = Utility.dateTimeFormat.format(mark.time.toLong())
        )
    }
}