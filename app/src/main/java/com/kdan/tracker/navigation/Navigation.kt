package com.kdan.tracker.navigation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kdan.authorization.viewmodel.AuthViewModel
import com.kdan.authorization.navigation.RoutesAuth
import com.kdan.authorization.screen.FragmentRestorePassword
import com.kdan.authorization.screen.FragmentSignIn
import com.kdan.authorization.screen.FragmentSignUp
import com.kdan.tracker.MainActivity
import com.kdan.tracker.screen.FragmentTracker

@Composable
fun Navigation(
    navController: NavHostController,
    applicationContext: Context,
    activity: MainActivity,
    viewModel: AuthViewModel = viewModel(),
) {

    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.isUserLoggedIn) {
                Route.fragmentTracker
            } else RoutesAuth.fragmentSignIn
        ) {
            composable(route = Route.fragmentTracker) {
                FragmentTracker(
                    navController,
                    applicationContext,
                    activity
                )
            }
            composable(route = RoutesAuth.fragmentSignIn) {
                FragmentSignIn(
                    navController,
                    applicationContext,
                    routeToTracker = Route.fragmentTracker
                )
            }
            composable(route = RoutesAuth.fragmentSignUp) {
                FragmentSignUp(
                    navController,
                    applicationContext
                )
            }
            composable(route = RoutesAuth.fragmentRestorePassword) {
                FragmentRestorePassword(
                    navController,
                    applicationContext
                )
            }
        }
    }
}