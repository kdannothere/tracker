package com.kdan.tracker

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
import com.kdan.authorization.AuthViewModel
import com.kdan.authorization.RoutesAuth
import com.kdan.authorization.screens.ViewRestorePassword
import com.kdan.authorization.screens.ViewSignIn
import com.kdan.authorization.screens.ViewSignUp
import com.kdan.tracker.screen.ViewTracker

@Composable
fun Navigation(
    navController: NavHostController,
    applicationContext: Context,
    viewModel: AuthViewModel = viewModel(),
) {
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        NavHost(
            navController = navController,
            startDestination = if (viewModel.isUserLoggedIn) {
                Routes.ViewTracker
            } else RoutesAuth.ViewSignIn
        ) {
            composable(route = Routes.ViewTracker) {
                ViewTracker(
                    navController,
                    applicationContext
                )
            }
            composable(route = RoutesAuth.ViewSignIn) {
                ViewSignIn(
                    navController,
                    applicationContext,
                    routeToTracker = Routes.ViewTracker
                )
            }
            composable(route = RoutesAuth.ViewSignUp) {
                ViewSignUp(
                    navController,
                    applicationContext
                )
            }
            composable(route = RoutesAuth.ViewRestorePassword) {
                ViewRestorePassword(
                    navController,
                    applicationContext
                )
            }
        }
    }
}

object Routes {
    const val ViewTracker = "ViewTracker"
}