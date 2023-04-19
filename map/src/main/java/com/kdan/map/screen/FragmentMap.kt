package com.kdan.map.screen

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.google.maps.android.compose.GoogleMap
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.map.screen.elements.ShowOnMap
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
                    //Log.d("kdanMap", "onMapLoaded")
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
}