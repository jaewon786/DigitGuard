package com.digitguard.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = BackgroundWhite,
    background = BackgroundWhite,
    onBackground = TextBlack,
    surface = BackgroundWhite,
    onSurface = TextBlack,
    error = WarningRed,
)

@Composable
fun DigitGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
