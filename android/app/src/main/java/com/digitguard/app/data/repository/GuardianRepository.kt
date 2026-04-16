package com.digitguard.app.data.repository

import com.digitguard.app.data.remote.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuardianRepository @Inject constructor(
    private val guardianApi: GuardianApi,
) {
    suspend fun requestLinkCode(guardianId: String): Result<LinkResponse> = runCatching {
        guardianApi.requestLink(LinkRequest(guardianId = guardianId))
    }

    suspend fun acceptByCode(code: String, protectedId: String): Result<AcceptByCodeResponse> = runCatching {
        guardianApi.acceptByCode(AcceptByCodeRequest(code = code, protectedId = protectedId))
    }

    suspend fun getProtectedUsers(guardianId: String): Result<List<ProtectedUserDto>> = runCatching {
        guardianApi.getProtectedUsers(guardianId).users
    }

    suspend fun getDashboard(userId: String): Result<DashboardResponse> = runCatching {
        guardianApi.getDashboard(userId)
    }

    suspend fun respondInstallRequest(requestId: String, decision: String): Result<Map<String, Any>> = runCatching {
        guardianApi.respondInstallRequest(requestId, mapOf("decision" to decision))
    }
}
