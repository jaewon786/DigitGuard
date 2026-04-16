package com.digitguard.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.data.preferences.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val settings: StateFlow<UserSettings> = userPreferences.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun setProtectionLevel(level: Int) {
        viewModelScope.launch { userPreferences.setProtectionLevel(level) }
    }

    fun setFontSizeLevel(level: Int) {
        viewModelScope.launch { userPreferences.setFontSizeLevel(level) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setSoundEnabled(enabled) }
    }

    fun setTtsEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setTtsEnabled(enabled) }
    }
}
