package com.digitguard.app.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitguard.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferences.completeOnboarding()
        }
    }

    fun setUserRole(role: String, name: String = "") {
        viewModelScope.launch {
            userPreferences.setUserInfo(role = role, userId = "dev-${System.currentTimeMillis()}", userName = name)
            userPreferences.completeOnboarding()
        }
    }
}
