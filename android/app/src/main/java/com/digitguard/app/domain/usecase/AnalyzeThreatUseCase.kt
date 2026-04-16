package com.digitguard.app.domain.usecase

import com.digitguard.app.data.repository.ThreatRepository
import com.digitguard.app.domain.model.ThreatResult
import javax.inject.Inject

class AnalyzeThreatUseCase @Inject constructor(
    private val repository: ThreatRepository,
) {
    suspend operator fun invoke(text: String, sourcePackage: String): ThreatResult {
        return repository.analyzeThreat(text, sourcePackage)
    }
}
