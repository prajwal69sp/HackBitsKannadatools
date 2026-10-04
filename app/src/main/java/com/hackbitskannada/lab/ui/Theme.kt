package com.hackbitskannada.lab.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

private val LabDark = darkColorScheme(
    primary = Color(0xFFA4E894),
    onPrimary = Color(0xFF17351D),
    secondary = Color(0xFF72D3C0),
    tertiary = Color(0xFFE2B675),
    background = Color(0xFF101512),
    surface = Color(0xFF171F1A),
    surfaceVariant = Color(0xFF26332B),
    onSurface = Color(0xFFE6EEE7),
    onSurfaceVariant = Color(0xFFB7C5B9),
    error = Color(0xFFFFB4AB)
)

private val LabLight = lightColorScheme(
    primary = Color(0xFF246B3B),
    onPrimary = Color.White,
    secondary = Color(0xFF006B62),
    tertiary = Color(0xFF925B17),
    background = Color(0xFFF3F6F0),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE1E9E0),
    onSurface = Color(0xFF172019),
    onSurfaceVariant = Color(0xFF3F5144),
    error = Color(0xFFBA1A1A)
)

@Composable
fun HackBitsTheme(themeName: String, fontScale: Float, content: @Composable () -> Unit) {
    val dark = when (themeName) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }
    val baseTypography = Typography()
    val scaledTypography = Typography(
        bodyLarge = baseTypography.bodyLarge.copy(fontSize = 16.sp * fontScale),
        bodyMedium = baseTypography.bodyMedium.copy(fontSize = 14.sp * fontScale),
        bodySmall = baseTypography.bodySmall.copy(fontSize = 12.sp * fontScale),
        titleLarge = baseTypography.titleLarge.copy(fontSize = 22.sp * fontScale),
        titleMedium = baseTypography.titleMedium.copy(fontSize = 16.sp * fontScale),
        headlineMedium = baseTypography.headlineMedium.copy(fontSize = 28.sp * fontScale),
        labelLarge = baseTypography.labelLarge.copy(fontSize = 14.sp * fontScale)
    )
    MaterialTheme(
        colorScheme = if (dark) LabDark else LabLight,
        typography = scaledTypography,
        content = content
    )
}

val CodeFont: FontFamily = FontFamily.Monospace
val BrandFont: FontFamily = FontFamily.SansSerif
