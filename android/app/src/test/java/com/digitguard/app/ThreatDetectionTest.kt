package com.digitguard.app

import com.digitguard.app.domain.model.ThreatLevel
import org.junit.Assert.*
import org.junit.Test

/**
 * 위험 감지 엔진 로컬 패턴 매칭 단위 테스트
 */
class ThreatDetectionTest {

    private val patterns = listOf(
        TestPattern("바이러스.*발견", ThreatLevel.HIGH),
        TestPattern("바이러스.*감염", ThreatLevel.HIGH),
        TestPattern("핸드폰.*감염", ThreatLevel.HIGH),
        TestPattern("지금.*치료", ThreatLevel.HIGH),
        TestPattern("개인정보.*유출", ThreatLevel.HIGH),
        TestPattern("보안.*앱.*설치", ThreatLevel.MEDIUM),
        TestPattern("긴급.*업데이트", ThreatLevel.MEDIUM),
        TestPattern("배터리.*손상", ThreatLevel.MEDIUM),
    )

    @Test
    fun `위험 텍스트 HIGH 감지`() {
        val result = analyze("귀하의 핸드폰이 바이러스에 감염되었습니다")
        assertEquals(ThreatLevel.HIGH, result)
    }

    @Test
    fun `중간 위험 텍스트 MEDIUM 감지`() {
        val result = analyze("긴급 업데이트가 필요합니다")
        assertEquals(ThreatLevel.MEDIUM, result)
    }

    @Test
    fun `안전한 텍스트 NONE`() {
        val result = analyze("오늘 날씨가 좋습니다")
        assertEquals(ThreatLevel.NONE, result)
    }

    @Test
    fun `복합 패턴 HIGH 우선`() {
        val result = analyze("배터리가 손상되었고 바이러스가 발견되었습니다")
        assertEquals(ThreatLevel.HIGH, result)
    }

    @Test
    fun `빈 텍스트 NONE`() {
        val result = analyze("")
        assertEquals(ThreatLevel.NONE, result)
    }

    @Test
    fun `ThreatLevel severity 순서 확인`() {
        assertTrue(ThreatLevel.HIGH.severity > ThreatLevel.MEDIUM.severity)
        assertTrue(ThreatLevel.MEDIUM.severity > ThreatLevel.LOW.severity)
        assertTrue(ThreatLevel.LOW.severity > ThreatLevel.NONE.severity)
    }

    private fun analyze(text: String): ThreatLevel {
        var maxLevel = ThreatLevel.NONE
        for (pattern in patterns) {
            val regex = Regex(pattern.regex, RegexOption.IGNORE_CASE)
            if (regex.containsMatchIn(text)) {
                if (pattern.level.severity > maxLevel.severity) {
                    maxLevel = pattern.level
                }
            }
        }
        return maxLevel
    }

    private data class TestPattern(val regex: String, val level: ThreatLevel)
}
