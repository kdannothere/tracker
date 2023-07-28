package com.kdan.tracker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.screen.FragmentRestorePassword
import com.kdan.authorization.screen.FragmentSignIn
import com.kdan.authorization.screen.FragmentSignUp
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.map.navigation.RoutesMap
import com.kdan.map.screen.FragmentMap
import com.kdan.map.screen.FragmentSettings
import com.kdan.map.viewmodel.MapViewModel
import com.kdan.tracker.screen.FragmentTracker

@Composable
fun Navigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn) {
            RouteTracker.fragmentTracker
        } else RoutesAuth.fragmentSignIn
    ) {
        composable(route = RouteTracker.fragmentTracker) {
            FragmentTracker(
                navController,
                authViewModel
            )
        }
        composable(route = RoutesMap.fragmentMap) {
            FragmentMap(
                navController,
                RouteTracker.fragmentTracker,
                authViewModel,
                mapViewModel
            )
        }
        composable(route = RoutesMap.fragmentSettings) {
            FragmentSettings(
                navController,
                authViewModel,
                mapViewModel
            )
        }
        composable(route = RoutesAuth.fragmentSignIn) {
            FragmentSignIn(
                navController,
                RouteTracker.fragmentTracker,
                authViewModel
            )
        }
        composable(route = RoutesAuth.fragmentSignUp) {
            FragmentSignUp(
                navController,
                authViewModel
            )
        }
        composable(route = RoutesAuth.fragmentRestorePassword) {
            FragmentRestorePassword(
                navController,
                authViewModel
            )
        }
    }
}