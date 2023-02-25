package com.kdan.authorization.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kdan.authorization.AuthViewModel
import com.kdan.authorization.RoutesAuth

@Composable
fun ViewWelcome(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome!", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Your email:",
                fontSize = 20.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "${viewModel.auth.currentUser?.email}",
                fontSize = 20.sp)
            Spacer(modifier = Modifier.height(60.dp))
            TextButton(onClick = {
                viewModel.logOut()
                navController.navigate(RoutesAuth.ViewSignIn)
            }) {
                Text(text = "Log Out",
                    fontSize = 25.sp)
            }
        }
    }
}