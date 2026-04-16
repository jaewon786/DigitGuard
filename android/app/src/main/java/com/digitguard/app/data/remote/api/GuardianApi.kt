package com.digitguard.app.data.remote.api

import retrofit2.http.*

// ── 요청 DTO ────────────────────

data class LinkRequest(val guardianId: String, val protectedId: String? = null)
data class AcceptByCodeRequest(val code: String, val protectedId: String)

// ── 응답 DTO ────────────────────

data class LinkResponse(
    val message: String,
    val linkCode: String? = null,
    val linkId: String? = null,
)

data class AcceptByCodeResponse(
    val message: String,
    val guardianName: String? = null,
)

data class DashboardResponse(
    val userId: String,
    val userName: String,
    val status: String,
    val recentThreats: List<ThreatLogDto>,
    val pendingInstalls: List<InstallRequestDto>,
    val lastChecked: String,
)

data class ThreatLogDto(
    val id: String,
    val threat_type: String,
    val threat_level: String,
    val detected_text: String?,
    val action_taken: String?,
    val created_at: String,
)

data class InstallRequestDto(
    val id: String,
    val package_name: String,
    val app_name: String?,
    val risk_score: Int?,
    val guardian_decision: String,
)

data class ProtectedUsersResponse(val users: List<ProtectedUserDto>)

data class ProtectedUserDto(
    val id: String,
    val name: String,
    val phone: String?,
    val linkId: String,
)

// ── API ─────────────────────────

interface GuardianApi {
    @POST("guardian/link")
    suspend fun requestLink(@Body request: LinkRequest): LinkResponse

    @POST("guardian/link/accept-by-code")
    suspend fun acceptByCode(@Body request: AcceptByCodeRequest): AcceptByCodeResponse

    @GET("guardian/protected-users")
    suspend fun getProtectedUsers(@Query("guardianId") guardianId: String): ProtectedUsersResponse

    @GET("guardian/dashboard/{userId}")
    suspend fun getDashboard(@Path("userId") userId: String): DashboardResponse

    @PUT("apps/install-request/{id}")
    suspend fun respondInstallRequest(
        @Path("id") id: String,
        @Body decision: Map<String, String>,
    ): Map<String, Any>
}
