package com.kdan.map.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavHostController
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.map.screen.elements.DateTimePickers
import com.kdan.map.viewmodel.MapViewModel

@Composable
fun FragmentSettings(
    navController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner,
    authViewModel: AuthViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
    mapViewModel: MapViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner
    ),
) {
    val lazyColumnState = rememberLazyListState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LazyColumn(
            state = lazyColumnState,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            item {
                TextButton(
                    onClick = {
                        mapViewModel.clearLocalMarks()
                        authViewModel.logOut()
                        navController.navigate(RoutesAuth.fragmentSignIn)
                    }
                ) {
                    Text(
                        text = "Log out",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextButton(
                    onClick = {
                        mapViewModel.loadMarksFromCloud(
                            email = authViewModel.getUserEmail()
                        )
                    }
                ) {
                    Text(
                        text = "Load marks from cloud",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                DateTimePickers(
                    viewModelStoreOwner
                )
                TextButton(
                    onClick = {
                        navController.navigate(RoutesMap.fragmentMap)
                    }
                ) {
                    Text(
                        text = "Back to map",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}