package com.kdan.authorization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.kdan.authorization.ui.theme.AuthorizationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthorizationTheme {
                val navController = rememberNavController()
                Navigation(navController, baseContext)
            }
        }
    }


}