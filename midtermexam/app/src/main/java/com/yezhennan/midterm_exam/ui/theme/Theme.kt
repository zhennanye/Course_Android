package com.yezhennan.midterm_exam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = GreenDark,
    secondary = GreenLight,
    onSecondary = GreenDark,
    tertiary = GreenLight,
    onTertiary = GreenDark,
    onBackground = GreenLight,
    onSurface = GreenLight,
    onSurfaceVariant = GreenLight
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenDark,
    secondary = GreenPrimary,
    onSecondary = GreenDark,
    tertiary = GreenPrimary,
    onTertiary = GreenDark,
    onBackground = GreenText,
    onSurface = GreenText,
    onSurfaceVariant = GreenText
)

@Composable
fun MidtermexamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}