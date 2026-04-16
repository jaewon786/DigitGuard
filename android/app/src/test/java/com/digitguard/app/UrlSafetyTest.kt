package com.digitguard.app

import org.junit.Assert.*
import org.junit.Test

/**
 * URL 안전 검사 로직 단위 테스트
 */
class UrlSafetyTest {

    private val dangerousPatterns = listOf(
        Regex("bit\\.ly/[a-zA-Z0-9]+", RegexOption.IGNORE_CASE),
        Regex("tinyurl\\.com/[a-zA-Z0-9]+", RegexOption.IGNORE_CASE),
        Regex("\\.(tk|ml|ga|cf|gq)/", RegexOption.IGNORE_CASE),
        Regex("login.*\\.php", RegexOption.IGNORE_CASE),
    )

    private val phishingDomains = setOf(
        "fake-bank.com",
        "security-alert.net",
    )

    @Test
    fun `안전한 URL`() {
        assertTrue(checkUrl("https://google.com"))
        assertTrue(checkUrl("https://naver.com"))
    }

    @Test
    fun `피싱 도메인 차단`() {
        assertFalse(checkUrl("https://fake-bank.com/account"))
        assertFalse(checkUrl("http://security-alert.net/verify"))
    }

    @Test
    fun `의심 패턴 차단`() {
        assertFalse(checkUrl("https://bit.ly/abc123"))
        assertFalse(checkUrl("https://example.tk/page"))
        assertFalse(checkUrl("https://evil.com/login_verify.php"))
    }

    @Test
    fun `빈 URL`() {
        assertTrue(checkUrl(""))
    }

    private fun checkUrl(url: String): Boolean {
        val domain = url
            .removePrefix("https://")
            .removePrefix("http://")
            .split("/")
            .firstOrNull()
            ?.lowercase() ?: ""

        if (domain in phishingDomains) return false

        for (pattern in dangerousPatterns) {
            if (pattern.containsMatchIn(url)) return false
        }

        return true
    }
}
