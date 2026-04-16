package com.digitguard.app.data.repository

import com.digitguard.app.data.preferences.UserPreferences
import com.digitguard.app.data.remote.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val deviceApi: DeviceApi,
    private val userPreferences: UserPreferences,
) {
    suspend fun register(name: String, phone: String, role: String): Result<AuthResponse> = runCatching {
        val firebaseUid = "dev-${System.currentTimeMillis()}" // TODO: Firebase Auth UID
        val response = authApi.register(RegisterRequest(name = name, phone = phone, role = role))
        userPreferences.setUserInfo(role = role, userId = firebaseUid, userName = name)
        response
    }

    suspend fun registerFcmToken(userId: String, token: String): Result<Unit> = runCatching {
        deviceApi.registerFcmToken(FcmTokenRequest(userId = userId, fcmToken = token))
    }
}
