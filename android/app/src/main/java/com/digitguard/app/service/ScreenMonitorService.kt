package com.digitguard.app.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.digitguard.app.domain.model.ThreatLevel
import java.util.Locale

class ScreenMonitorService : AccessibilityService() {

    private var tts: TextToSpeech? = null
    private var lastAnalyzedText = ""
    private var lastAnalyzedTime = 0L
    private val analyzeDebounceMs = 2000L
    private var isScreenOn = true

    // 컴파일된 패턴 캐시 (성능 최적화)
    private val compiledPatterns: List<CompiledPattern> by lazy {
        localPatterns.mapNotNull { entry ->
            try {
                CompiledPattern(Regex(entry.regex, RegexOption.IGNORE_CASE), entry.level, entry.category)
            } catch (_: Exception) { null }
        }
    }

    private val localPatterns = listOf(
        PatternEntry("바이러스.*발견", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("바이러스.*감염", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("악성코드.*노출", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("악성코드.*발견", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("핸드폰.*감염", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("기기.*감염", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("지금.*치료", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("즉시.*제거", ThreatLevel.HIGH, "fake_virus"),
        PatternEntry("개인정보.*유출", ThreatLevel.HIGH, "fake_security"),
        PatternEntry("기기.*해킹", ThreatLevel.HIGH, "fake_security"),
        PatternEntry("보안.*위협.*감지", ThreatLevel.HIGH, "fake_security"),
        PatternEntry("보안.*앱.*설치", ThreatLevel.MEDIUM, "fake_security"),
        PatternEntry("긴급.*업데이트", ThreatLevel.MEDIUM, "fake_security"),
        PatternEntry("배터리.*손상", ThreatLevel.MEDIUM, "fake_virus"),
        PatternEntry("계좌.*정지", ThreatLevel.HIGH, "phishing"),
        PatternEntry("본인.*확인.*링크", ThreatLevel.HIGH, "phishing"),
        PatternEntry("당첨.*축하", ThreatLevel.HIGH, "phishing"),
        PatternEntry("계좌.*이체.*요청", ThreatLevel.HIGH, "voice_phishing"),
        PatternEntry("안전.*계좌.*이동", ThreatLevel.HIGH, "voice_phishing"),
        PatternEntry("검찰.*수사관", ThreatLevel.HIGH, "voice_phishing"),
    )

    // 화면 ON/OFF 감지 (배터리 최적화)
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isScreenOn = intent?.action == Intent.ACTION_SCREEN_ON
        }
    }

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.KOREAN
                tts?.setSpeechRate(0.9f)
            }
        }

        // 화면 ON/OFF 리시버 등록
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)

        // 현재 화면 상태 확인
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        isScreenOn = pm.isInteractive
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        // 화면 꺼짐 시 분석 건너뛰기 (배터리 절약)
        if (!isScreenOn) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) {
            val pkg = event.packageName?.toString() ?: return
            if (pkg == packageName) return

            // 디바운스
            val now = System.currentTimeMillis()
            if (now - lastAnalyzedTime < analyzeDebounceMs) return
            lastAnalyzedTime = now

            val rootNode = rootInActiveWindow ?: return
            val screenText = try {
                extractAllText(rootNode, maxDepth = 10)
            } finally {
                rootNode.recycle()
            }

            if (screenText == lastAnalyzedText || screenText.length < 10) return
            lastAnalyzedText = screenText

            analyzeLocally(screenText)
        }
    }

    private fun analyzeLocally(text: String) {
        var maxLevel = ThreatLevel.NONE

        for (pattern in compiledPatterns) {
            if (pattern.regex.containsMatchIn(text)) {
                if (pattern.level.severity > maxLevel.severity) {
                    maxLevel = pattern.level
                }
                // HIGH가 발견되면 바로 중단 (성능 최적화)
                if (maxLevel == ThreatLevel.HIGH) break
            }
        }

        when (maxLevel) {
            ThreatLevel.HIGH -> showWarningOverlay()
            ThreatLevel.MEDIUM -> {
                showNotification("주의가 필요합니다", "의심스러운 내용이 감지되었습니다.")
                speakWarning("주의가 필요합니다. 의심스러운 내용이 감지되었습니다.")
            }
            else -> { }
        }
    }

    private fun showWarningOverlay() {
        val intent = Intent(this, WarningOverlayService::class.java)
        startService(intent)
    }

    private fun showNotification(title: String, message: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager

        val channel = android.app.NotificationChannel(
            "digitguard_warning", "위험 경고",
            android.app.NotificationManager.IMPORTANCE_HIGH,
        )
        nm.createNotificationChannel(channel)

        val notification = android.app.Notification.Builder(this, "digitguard_warning")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun speakWarning(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "warning")
    }

    // 트리 깊이 제한으로 성능 보호
    private fun extractAllText(node: AccessibilityNodeInfo, maxDepth: Int = 10, depth: Int = 0): String {
        if (depth >= maxDepth) return ""
        val builder = StringBuilder()
        node.text?.let { builder.append(it).append(' ') }
        node.contentDescription?.let { builder.append(it).append(' ') }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                builder.append(extractAllText(it, maxDepth, depth + 1))
                it.recycle()
            }
        }
        return builder.toString()
    }

    override fun onInterrupt() {
        tts?.stop()
    }

    override fun onDestroy() {
        tts?.shutdown()
        try { unregisterReceiver(screenReceiver) } catch (_: Exception) { }
        super.onDestroy()
    }

    private data class PatternEntry(val regex: String, val level: ThreatLevel, val category: String)
    private data class CompiledPattern(val regex: Regex, val level: ThreatLevel, val category: String)
}
