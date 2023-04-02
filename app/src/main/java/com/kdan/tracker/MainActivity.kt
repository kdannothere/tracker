package com.kdan.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.kdan.tracker.navigation.Navigation
import com.kdan.tracker.ui.theme.TrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackerTheme {
                val navController = rememberNavController()
                Navigation(
                    navController,
                    applicationContext,
                    this
                )
            }
        }
    }
}