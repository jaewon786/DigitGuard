package com.digitguard.app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

data class UrlCheckRequest(val url: String)

data class UrlCheckResponse(
    val url: String,
    val safe: Boolean,
    val threats: List<String>,
    val checkedAt: String,
)

interface UrlCheckApi {
    @POST("urls/check")
    suspend fun checkUrl(@Body request: UrlCheckRequest): UrlCheckResponse
}
