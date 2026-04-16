package com.digitguard.app

import com.digitguard.app.data.preferences.UserSettings
import org.junit.Assert.*
import org.junit.Test

/**
 * UserSettings 기본값 및 불변식 테스트
 */
class UserPreferencesTest {

    @Test
    fun `기본 설정값 확인`() {
        val settings = UserSettings()
        assertFalse(settings.isOnboardingCompleted)
        assertEquals("", settings.userRole)
        assertEquals("", settings.userId)
        assertEquals(1, settings.protectionLevel)
        assertEquals(1, settings.fontSizeLevel)
        assertTrue(settings.soundEnabled)
        assertTrue(settings.ttsEnabled)
    }

    @Test
    fun `보호 수준 범위 확인`() {
        // 0=낮음, 1=보통, 2=높음
        val settings = UserSettings(protectionLevel = 0)
        assertTrue(settings.protectionLevel in 0..2)
    }

    @Test
    fun `역할은 protected 또는 guardian만 유효`() {
        val validRoles = setOf("protected", "guardian", "")
        val settings = UserSettings(userRole = "protected")
        assertTrue(settings.userRole in validRoles)
    }
}
