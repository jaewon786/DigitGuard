package com.digitguard.app.service

import com.digitguard.app.data.remote.api.UrlCheckApi
import com.digitguard.app.data.remote.api.UrlCheckRequest
import com.digitguard.app.data.remote.api.UrlCheckResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSafetyChecker @Inject constructor(
    private val urlCheckApi: UrlCheckApi,
) {
    // 로컬 위험 URL 패턴 (오프라인 동작용)
    private val dangerousPatterns = listOf(
        Regex("bit\\.ly/[a-zA-Z0-9]+", RegexOption.IGNORE_CASE),
        Regex("tinyurl\\.com/[a-zA-Z0-9]+", RegexOption.IGNORE_CASE),
        Regex("\\.(tk|ml|ga|cf|gq)/", RegexOption.IGNORE_CASE),
        Regex("login.*\\.php", RegexOption.IGNORE_CASE),
        Regex("verify.*account", RegexOption.IGNORE_CASE),
        Regex("secure.*update.*\\.com", RegexOption.IGNORE_CASE),
    )

    private val knownPhishingDomains = setOf(
        "fake-bank.com",
        "security-alert.net",
        "account-verify.info",
    )

    // 로컬 1차 검사
    fun checkLocally(url: String): LinkCheckResult {
        // 의심스러운 패턴 매칭
        for (pattern in dangerousPatterns) {
            if (pattern.containsMatchIn(url)) {
                return LinkCheckResult(
                    safe = false,
                    reason = "의심스러운 URL 패턴이 감지되었습니다.",
                )
            }
        }

        // 피싱 도메인 확인
        val domain = extractDomain(url)
        if (domain in knownPhishingDomains) {
            return LinkCheckResult(
                safe = false,
                reason = "알려진 피싱 사이트입니다.",
            )
        }

        return LinkCheckResult(safe = true, reason = "")
    }

    // 서버 2차 검사
    suspend fun checkRemotely(url: String): UrlCheckResponse {
        return try {
            urlCheckApi.checkUrl(UrlCheckRequest(url))
        } catch (e: Exception) {
            // 네트워크 오류 시 로컬 결과만 사용
            val local = checkLocally(url)
            UrlCheckResponse(
                url = url,
                safe = local.safe,
                threats = if (!local.safe) listOf(local.reason) else emptyList(),
                checkedAt = "",
            )
        }
    }

    private fun extractDomain(url: String): String {
        return url
            .removePrefix("https://")
            .removePrefix("http://")
            .split("/")
            .firstOrNull()
            ?.lowercase() ?: ""
    }

    data class LinkCheckResult(
        val safe: Boolean,
        val reason: String,
    )
}
