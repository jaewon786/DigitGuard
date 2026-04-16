package com.digitguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.ui.navigation.NavGraph
import com.digitguard.app.ui.theme.DigitGuardTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by userPreferences.settings.collectAsState(
                initial = com.digitguard.app.data.preferences.UserSettings()
            )
            DigitGuardTheme {
                NavGraph(
                    startFromOnboarding = !settings.isOnboardingCompleted,
                )
            }
        }
    }
}
