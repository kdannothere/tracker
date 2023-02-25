package com.kdan.authorization

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kdan.authorization.screens.ViewRestorePassword
import com.kdan.authorization.screens.ViewSignIn
import com.kdan.authorization.screens.ViewSignUp
import com.kdan.authorization.screens.ViewWelcome

@Composable
fun Navigation(
    navController: NavHostController,
    context: Context,
) {
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        NavHost(
            navController = navController,
            startDestination = RoutesAuth.ViewSignIn
        ) {
            composable(route = RoutesAuth.ViewSignIn) { ViewSignIn(navController, context) }
            composable(route = RoutesAuth.ViewRestorePassword) {
                ViewRestorePassword(navController, context)
            }
            composable(route = RoutesAuth.ViewSignUp) { ViewSignUp(navController, context) }
            composable(route = RoutesAuth.ViewWelcome) { ViewWelcome(navController) }
        }
    }
}

object RoutesAuth {
    const val ViewSignIn = "ViewSignIn"
    const val ViewRestorePassword = "ViewRestorePassword"
    const val ViewSignUp = "ViewSignUp"
    const val ViewWelcome = "ViewWelcome"
}