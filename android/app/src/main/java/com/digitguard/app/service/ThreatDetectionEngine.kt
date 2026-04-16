package com.digitguard.app.service

import com.digitguard.app.data.repository.ThreatRepository
import com.digitguard.app.domain.model.ThreatResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatDetectionEngine @Inject constructor(
    private val repository: ThreatRepository,
) {
    suspend fun analyze(text: String, sourcePackage: String): ThreatResult {
        return repository.analyzeThreat(text, sourcePackage)
    }
}
