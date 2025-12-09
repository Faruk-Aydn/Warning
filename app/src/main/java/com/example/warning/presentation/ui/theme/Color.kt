package com.example.warning.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ocean (Blue/Trust)
val OceanLightPrimary = Color(0xFF0061A4)
val OceanLightOnPrimary = Color(0xFFFFFFFF)
val OceanLightSecondary = Color(0xFF535F70)
val OceanLightBackground = Color(0xFFFDFCFF)
val OceanLightSurface = Color(0xFFFDFCFF)

val OceanDarkPrimary = Color(0xFF99CBFF)
val OceanDarkOnPrimary = Color(0xFF003258)
val OceanDarkSecondary = Color(0xFFBBC7DB)
val OceanDarkBackground = Color(0xFF1A1C1E)
val OceanDarkSurface = Color(0xFF1A1C1E)

// Nature (Green/Calm)
val NatureLightPrimary = Color(0xFF4C662B)
val NatureLightOnPrimary = Color(0xFFFFFFFF)
val NatureLightSecondary = Color(0xFF586249)
val NatureLightBackground = Color(0xFFF9FBF1)
val NatureLightSurface = Color(0xFFF9FBF1)

val NatureDarkPrimary = Color(0xFFCDEDA3)
val NatureDarkOnPrimary = Color(0xFF1A3701)
val NatureDarkSecondary = Color(0xFFBFCBAD)
val NatureDarkBackground = Color(0xFF1B1D18)
val NatureDarkSurface = Color(0xFF1B1D18)

// Sunset (Orange/Warm)
val SunsetLightPrimary = Color(0xFFA04000)
val SunsetLightOnPrimary = Color(0xFFFFFFFF)
val SunsetLightSecondary = Color(0xFF77574E)
val SunsetLightBackground = Color(0xFFFFDBCC)
val SunsetLightSurface = Color(0xFFFFDBCC)

val SunsetDarkPrimary = Color(0xFFFFB59B)
val SunsetDarkOnPrimary = Color(0xFF5C1900)
val SunsetDarkSecondary = Color(0xFFE7BDB2)
val SunsetDarkBackground = Color(0xFF201A18)
val SunsetDarkSurface = Color(0xFF201A18)

// Lavender (Purple/Elegant)
val LavenderLightPrimary = Color(0xFF6750A4)
val LavenderLightOnPrimary = Color(0xFFFFFFFF)
val LavenderLightSecondary = Color(0xFF625B71)
val LavenderLightBackground = Color(0xFFFFFBFF)
val LavenderLightSurface = Color(0xFFFFFBFF)

val LavenderDarkPrimary = Color(0xFFD0BCFF)
val LavenderDarkOnPrimary = Color(0xFF381E72)
val LavenderDarkSecondary = Color(0xFFCCC2DC)
val LavenderDarkBackground = Color(0xFF1C1B1F)
val LavenderDarkSurface = Color(0xFF1C1B1F)

// Cyber (Teal/Neon)
val CyberLightPrimary = Color(0xFF006874)
val CyberLightOnPrimary = Color(0xFFFFFFFF)
val CyberLightSecondary = Color(0xFF4A6367)
val CyberLightBackground = Color(0xFFFAFDFD)
val CyberLightSurface = Color(0xFFFAFDFD)

val CyberDarkPrimary = Color(0xFF4ED8E9)
val CyberDarkOnPrimary = Color(0xFF00363D)
val CyberDarkSecondary = Color(0xFFB1CBD0)
val CyberDarkBackground = Color(0xFF191C1D)
val CyberDarkSurface = Color(0xFF191C1D)

/**
 * Temporary bridge so existing composables that reference [AppColorScheme]
 * automatically read from the current Material3 color scheme.
 */
object AppColorScheme {
    val primary: Color
        @Composable get() = MaterialTheme.colorScheme.primary
    val secondary: Color
        @Composable get() = MaterialTheme.colorScheme.secondary
    val inactive: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceVariant
    val error: Color
        @Composable get() = MaterialTheme.colorScheme.error
    val success: Color
        @Composable get() = MaterialTheme.colorScheme.tertiary
    val backgroundDark: Color
        @Composable get() = MaterialTheme.colorScheme.background
    val neutralLight: Color
        @Composable get() = MaterialTheme.colorScheme.surface
    val warning: Color
        @Composable get() = MaterialTheme.colorScheme.secondary
    val info: Color
        @Composable get() = MaterialTheme.colorScheme.primary
    val divider: Color
        @Composable get() = MaterialTheme.colorScheme.outline
    val successGreen: Color
        @Composable get() = MaterialTheme.colorScheme.tertiary
}