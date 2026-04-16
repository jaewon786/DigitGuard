package com.digitguard.app.data.repository

import com.digitguard.app.data.remote.api.EducationApi
import com.digitguard.app.data.remote.api.EducationContentDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EducationRepository @Inject constructor(
    private val educationApi: EducationApi,
) {
    suspend fun getContents(): Result<List<EducationContentDto>> = runCatching {
        educationApi.getContents().contents
    }

    suspend fun getContentById(id: String): Result<EducationContentDto> = runCatching {
        educationApi.getContentById(id)
    }
}
