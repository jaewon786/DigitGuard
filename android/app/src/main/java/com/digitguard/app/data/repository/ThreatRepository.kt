package com.digitguard.app.data.repository

import com.digitguard.app.data.local.dao.ThreatPatternDao
import com.digitguard.app.data.remote.api.AnalyzeRequest
import com.digitguard.app.data.remote.api.ThreatApi
import com.digitguard.app.domain.model.ThreatLevel
import com.digitguard.app.domain.model.ThreatResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatRepository @Inject constructor(
    private val threatApi: ThreatApi,
    private val threatPatternDao: ThreatPatternDao,
) {
    // 1차 로컬 분석 -> 2차 서버 분석
    suspend fun analyzeThreat(text: String, sourcePackage: String): ThreatResult {
        // 1차: 로컬 패턴 매칭
        val localResult = analyzeLocally(text)
        if (localResult.threatLevel == ThreatLevel.HIGH) {
            return localResult
        }

        // 2차: 서버 정밀 분석
        return try {
            threatApi.analyze(AnalyzeRequest(text, sourcePackage))
        } catch (e: Exception) {
            localResult
        }
    }

    private suspend fun analyzeLocally(text: String): ThreatResult {
        val patterns = threatPatternDao.getActivePatterns()
        var maxLevel = ThreatLevel.NONE
        val matched = mutableListOf<String>()

        for (pattern in patterns) {
            val regex = Regex(pattern.pattern, RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(text)) {
                matched.add(pattern.pattern)
                val level = try {
                    ThreatLevel.valueOf(pattern.threatLevel.uppercase())
                } catch (_: IllegalArgumentException) {
                    ThreatLevel.LOW
                }
                if (level.severity > maxLevel.severity) maxLevel = level
            }
        }

        return ThreatResult(
            threatLevel = maxLevel,
            matchedPatterns = matched,
            recommendation = if (matched.isNotEmpty()) "이것은 거짓 광고입니다. 무시하고 뒤로가기를 눌러주세요." else "",
        )
    }

    suspend fun getLocalPatternCount(): Int {
        return threatPatternDao.getActivePatterns().size
    }
}
