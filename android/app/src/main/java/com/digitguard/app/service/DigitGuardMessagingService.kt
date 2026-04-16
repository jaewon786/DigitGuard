package com.digitguard.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import com.digitguard.app.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DigitGuardMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: 서버에 FCM 토큰 등록/갱신
        // RetrofitClient.api.registerFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data["type"] ?: ""
        val title: String
        val body: String

        when (type) {
            "threat_detected" -> {
                val level = message.data["threatLevel"] ?: "unknown"
                val category = message.data["category"] ?: ""
                title = "⚠ 위험 감지"
                body = categoryToKorean(category) + " 위험이 감지되었습니다. (위험도: ${levelToKorean(level)})"
            }
            "install_request" -> {
                val appName = message.data["appName"] ?: "알 수 없는 앱"
                title = "📱 앱 설치 승인 요청"
                body = "${appName} 앱 설치를 승인해 주세요."
            }
            "install_decision" -> {
                val decision = message.data["decision"] ?: ""
                val appName = message.data["appName"] ?: ""
                title = if (decision == "approved") "✅ 설치 승인됨" else "❌ 설치 거부됨"
                body = "${appName} 앱 설치가 ${if (decision == "approved") "승인" else "거부"}되었습니다."
            }
            "guardian_linked" -> {
                val guardianName = message.data["guardianName"] ?: "보호자"
                title = "🔗 보호자 연결"
                body = "${guardianName}님과 연결되었습니다."
            }
            else -> {
                title = message.notification?.title ?: "DigitGuard"
                body = message.notification?.body ?: ""
            }
        }

        showNotification(title, body, type)
    }

    private fun showNotification(title: String, body: String, type: String) {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = when (type) {
            "threat_detected" -> "digitguard_threat"
            "install_request" -> "digitguard_install"
            else -> "digitguard_general"
        }
        val channelName = when (type) {
            "threat_detected" -> "위험 감지 알림"
            "install_request" -> "앱 설치 승인"
            else -> "일반 알림"
        }
        val importance = if (type == "threat_detected") {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            NotificationManager.IMPORTANCE_DEFAULT
        }

        nm.createNotificationChannel(
            NotificationChannel(channelId, channelName, importance)
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_type", type)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun categoryToKorean(category: String): String = when (category) {
        "fake_virus" -> "허위 바이러스 경고"
        "fake_security" -> "허위 보안 경고"
        "phishing" -> "피싱"
        "voice_phishing" -> "보이스피싱"
        else -> "알 수 없는"
    }

    private fun levelToKorean(level: String): String = when (level) {
        "high" -> "높음"
        "medium" -> "보통"
        "low" -> "낮음"
        else -> level
    }
}
