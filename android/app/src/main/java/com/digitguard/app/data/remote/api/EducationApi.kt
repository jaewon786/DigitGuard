package com.digitguard.app.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path

data class EducationContentDto(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val steps: List<String>? = null,
)

data class EducationListResponse(
    val contents: List<EducationContentDto>,
    val count: Int,
)

interface EducationApi {
    @GET("education/contents")
    suspend fun getContents(): EducationListResponse

    @GET("education/contents/{id}")
    suspend fun getContentById(@Path("id") id: String): EducationContentDto
}
