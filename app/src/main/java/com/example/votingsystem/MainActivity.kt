package com.example.votingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.votingsystem.interfaces.screens.DashboardScreen
import com.example.votingsystem.interfaces.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }

            if (isLoggedIn) {
                DashboardScreen()
            } else {
                LoginScreen(onLoginSuccess = {
                    isLoggedIn = true
                })
            }
        }
    }
}
