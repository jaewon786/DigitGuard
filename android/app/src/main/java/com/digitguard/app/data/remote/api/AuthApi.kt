package com.digitguard.app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val name: String,
    val phone: String,
    val role: String,
    val firebaseUid: String? = null,
)

data class LoginRequest(
    val firebaseUid: String,
)

data class AuthResponse(
    val message: String,
    val user: UserDto? = null,
    val token: String? = null,
)

data class UserDto(
    val id: String,
    val name: String,
    val phone: String?,
    val role: String,
)

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
