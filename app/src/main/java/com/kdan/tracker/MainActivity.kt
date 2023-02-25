package com.kdan.tracker

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.kdan.tracker.ui.theme.TrackerTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        setContent {
            TrackerTheme {
                val navController = rememberNavController()
                Navigation(
                    navController,
                    applicationContext
                )
            }
        }
    }
}