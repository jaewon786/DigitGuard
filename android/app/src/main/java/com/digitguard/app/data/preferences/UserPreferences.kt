package com.digitguard.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserSettings(
    val isOnboardingCompleted: Boolean = false,
    val userRole: String = "",          // "protected" or "guardian"
    val userId: String = "",
    val userName: String = "",
    val guardianPhone: String = "",
    val protectionLevel: Int = 1,       // 0=낮음, 1=보통, 2=높음
    val fontSizeLevel: Int = 1,         // 0=보통, 1=크게, 2=매우 크게
    val soundEnabled: Boolean = true,
    val ttsEnabled: Boolean = true,
)

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val GUARDIAN_PHONE = stringPreferencesKey("guardian_phone")
        val PROTECTION_LEVEL = intPreferencesKey("protection_level")
        val FONT_SIZE_LEVEL = intPreferencesKey("font_size_level")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            isOnboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            userRole = prefs[Keys.USER_ROLE] ?: "",
            userId = prefs[Keys.USER_ID] ?: "",
            userName = prefs[Keys.USER_NAME] ?: "",
            guardianPhone = prefs[Keys.GUARDIAN_PHONE] ?: "",
            protectionLevel = prefs[Keys.PROTECTION_LEVEL] ?: 1,
            fontSizeLevel = prefs[Keys.FONT_SIZE_LEVEL] ?: 1,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            ttsEnabled = prefs[Keys.TTS_ENABLED] ?: true,
        )
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true }
    }

    suspend fun setUserInfo(role: String, userId: String, userName: String) {
        context.dataStore.edit {
            it[Keys.USER_ROLE] = role
            it[Keys.USER_ID] = userId
            it[Keys.USER_NAME] = userName
        }
    }

    suspend fun setGuardianPhone(phone: String) {
        context.dataStore.edit { it[Keys.GUARDIAN_PHONE] = phone }
    }

    suspend fun setProtectionLevel(level: Int) {
        context.dataStore.edit { it[Keys.PROTECTION_LEVEL] = level }
    }

    suspend fun setFontSizeLevel(level: Int) {
        context.dataStore.edit { it[Keys.FONT_SIZE_LEVEL] = level }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setTtsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TTS_ENABLED] = enabled }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
