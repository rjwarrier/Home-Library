package com.mj.homelibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mj.homelibrary.ui.HomeLibraryApp
import com.mj.homelibrary.ui.theme.HomeLibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeLibraryTheme {
                HomeLibraryApp()
            }
        }
    }
}
