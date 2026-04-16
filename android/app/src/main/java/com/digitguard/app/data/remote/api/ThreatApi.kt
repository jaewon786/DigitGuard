package com.digitguard.app.data.remote.api

import com.digitguard.app.domain.model.ThreatResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class AnalyzeRequest(
    val text: String,
    val sourcePackage: String,
)

data class PatternSyncResponse(
    val patterns: List<PatternDto>,
    val lastUpdated: String,
)

data class PatternDto(
    val pattern: String,
    val level: String,
    val category: String,
)

interface ThreatApi {
    @POST("threats/analyze")
    suspend fun analyze(@Body request: AnalyzeRequest): ThreatResult

    @GET("threats/patterns/sync")
    suspend fun syncPatterns(): PatternSyncResponse
}
