package com.digitguard.app.domain.model

data class ThreatResult(
    val threatLevel: ThreatLevel,
    val riskScore: Int = 0,
    val category: String = "",
    val matchedPatterns: List<String> = emptyList(),
    val recommendation: String = "",
    val shouldNotifyGuardian: Boolean = false,
)
