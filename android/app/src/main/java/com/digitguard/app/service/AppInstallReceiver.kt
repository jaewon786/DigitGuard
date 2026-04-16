package com.digitguard.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppInstallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_PACKAGE_ADDED) return
        if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return // 업데이트는 무시

        val packageName = intent.data?.schemeSpecificPart ?: return

        // 자체 앱 무시
        if (packageName == context.packageName) return

        val appName = getAppName(context, packageName)

        // 알림으로 보호자에게 알리기
        showInstallNotification(context, appName, packageName)

        // TODO: 서버에 앱 안전성 검사 요청
        // TODO: 위험 앱이면 보호자에게 FCM 푸시 알림 전송
    }

    private fun getAppName(context: Context, packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    private fun showInstallNotification(context: Context, appName: String, packageName: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "digitguard_install",
            "앱 설치 알림",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "새 앱이 설치되었을 때 알립니다"
        }
        nm.createNotificationChannel(channel)

        val notification = Notification.Builder(context, "digitguard_install")
            .setContentTitle("새 앱 설치됨")
            .setContentText("$appName 앱이 설치되었습니다. 안전성을 확인합니다.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        nm.notify(packageName.hashCode(), notification)
    }
}
