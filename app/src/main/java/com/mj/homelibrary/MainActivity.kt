package com.mj.homelibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.homelibrary.ui.HomeLibraryApp
import com.mj.homelibrary.ui.theme.HomeLibraryTheme
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val homeLibraryApplication = application as HomeLibraryApplication
        setContent {
            val appearanceSettings by homeLibraryApplication.appearanceRepository.settings.collectAsStateWithLifecycle()
            HomeLibraryTheme(appearanceSettings = appearanceSettings) {
                HomeLibraryApp()
            }
        }
    }
}
