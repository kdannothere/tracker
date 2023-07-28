package com.kdan.map.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.map.screen.elements.ShowOnMap
import com.kdan.map.viewmodel.MapViewModel

@Composable
fun FragmentMap(
    navController: NavHostController,
    routeToTracker: String,
    authViewModel: AuthViewModel,
    mapViewModel: MapViewModel,
) {
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true)
    }
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true))
    }
    Box {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = mapViewModel.cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapLoaded = {
                mapViewModel.run {
                    if (allMarks.isEmpty()) {
                        updateAllMarks(email = authViewModel.getUserEmail())
                    }
                }
            }
        ) {
            ShowOnMap(marksInTimeRange = mapViewModel.marksInTimeRange)
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