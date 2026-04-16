package com.digitguard.app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

data class FcmTokenRequest(val userId: String, val fcmToken: String)

interface DeviceApi {
    @POST("devices/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Map<String, String>
}
