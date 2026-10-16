package com.example.shefarmai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shefarmai.ui.navigation.AppNavigation
import com.example.shefarmai.ui.theme.SheFarmAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SheFarmAITheme {
                AppNavigation()
            }
        }
    }
}
