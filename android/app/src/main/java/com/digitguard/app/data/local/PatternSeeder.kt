package com.digitguard.app.data.local

import com.digitguard.app.data.local.dao.ThreatPatternDao
import com.digitguard.app.data.local.entity.LocalThreatPattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatternSeeder @Inject constructor(
    private val dao: ThreatPatternDao,
) {
    suspend fun seedIfEmpty() {
        val existing = dao.getActivePatterns()
        if (existing.isNotEmpty()) return

        val patterns = listOf(
            // 허위 바이러스 경고
            p("바이러스.*발견", "high", "fake_virus"),
            p("바이러스.*감염", "high", "fake_virus"),
            p("악성코드.*노출", "high", "fake_virus"),
            p("악성코드.*발견", "high", "fake_virus"),
            p("핸드폰.*감염", "high", "fake_virus"),
            p("기기.*감염", "high", "fake_virus"),
            p("지금.*치료", "high", "fake_virus"),
            p("즉시.*제거", "high", "fake_virus"),
            p("배터리.*손상", "medium", "fake_virus"),
            p("성능.*저하.*감지", "medium", "fake_virus"),
            p("메모리.*부족.*위험", "medium", "fake_virus"),

            // 허위 보안 경고
            p("개인정보.*유출", "high", "fake_security"),
            p("보안.*앱.*설치", "medium", "fake_security"),
            p("긴급.*업데이트", "medium", "fake_security"),
            p("기기.*해킹", "high", "fake_security"),
            p("보안.*위협.*감지", "high", "fake_security"),
            p("계정.*도용", "high", "fake_security"),
            p("비밀번호.*노출", "high", "fake_security"),

            // 피싱
            p("계좌.*정지", "high", "phishing"),
            p("본인.*확인.*링크", "high", "phishing"),
            p("택배.*조회.*클릭", "medium", "phishing"),
            p("당첨.*축하", "high", "phishing"),
            p("무료.*쿠폰.*지급", "medium", "phishing"),
            p("카드.*승인.*취소", "high", "phishing"),
            p("대출.*승인.*완료", "high", "phishing"),
            p("정부.*지원금.*신청", "medium", "phishing"),

            // 보이스피싱 관련
            p("검찰.*수사관", "high", "voice_phishing"),
            p("금융감독원", "high", "voice_phishing"),
            p("계좌.*이체.*요청", "high", "voice_phishing"),
            p("안전.*계좌.*이동", "high", "voice_phishing"),
        )

        dao.insertAll(patterns)
    }

    private var counter = 0
    private fun p(pattern: String, level: String, category: String) = LocalThreatPattern(
        id = "seed-${++counter}",
        pattern = pattern,
        patternType = "regex",
        threatLevel = level,
        category = category,
        lastSyncedAt = System.currentTimeMillis(),
    )
}
