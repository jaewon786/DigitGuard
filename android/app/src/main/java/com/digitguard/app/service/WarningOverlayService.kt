package com.digitguard.app.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class WarningOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: LinearLayout? = null
    private var tts: TextToSpeech? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        if (overlayView != null) return

        // 오버레이 권한 확인
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        )
        params.gravity = Gravity.CENTER

        overlayView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)

            // 경고 제목
            addView(TextView(context).apply {
                text = "⚠ 주의하세요!"
                textSize = 36f
                setTextColor(0xFFC62828.toInt())
                gravity = Gravity.CENTER
            })

            // 경고 메시지
            addView(TextView(context).apply {
                text = "\n이것은 거짓 광고입니다.\n절대 설치하지 마세요.\n"
                textSize = 24f
                setTextColor(0xFF212121.toInt())
                gravity = Gravity.CENTER
                setPadding(0, 32, 0, 48)
            })

            // 무시하고 돌아가기 버튼
            addView(Button(context).apply {
                text = "무시하고 돌아가기"
                textSize = 22f
                setBackgroundColor(0xFF2E7D32.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 24, 0, 24)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                lp.bottomMargin = 24
                layoutParams = lp
                setOnClickListener { dismissOverlay() }
            })

            // 보호자에게 전화하기 버튼
            addView(Button(context).apply {
                text = "보호자에게 전화하기"
                textSize = 22f
                setBackgroundColor(0xFF1565C0.toInt())
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 24, 0, 24)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                setOnClickListener { callGuardian() }
            })
        }

        windowManager?.addView(overlayView, params)

        // TTS 음성 경고
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.KOREAN
                tts?.setSpeechRate(0.9f)
                tts?.speak(
                    "주의하세요. 이것은 거짓 광고입니다. 절대 설치하지 마세요.",
                    TextToSpeech.QUEUE_FLUSH, null, "overlay-warning",
                )
            }
        }
    }

    private fun dismissOverlay() {
        tts?.stop()
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        stopSelf()
    }

    private fun callGuardian() {
        // TODO: SharedPreferences에서 보호자 전화번호 로드
        val phoneNumber = "tel:010-0000-0000"
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            startActivity(callIntent)
        } catch (_: SecurityException) {
            // CALL_PHONE 권한 없으면 다이얼러로
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(dialIntent)
        }
        dismissOverlay()
    }

    override fun onDestroy() {
        tts?.shutdown()
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        super.onDestroy()
    }
}
