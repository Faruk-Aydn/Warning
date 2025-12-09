package com.example.warning.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class AppTheme {
    OCEAN,
    NATURE,
    SUNSET,
    LAVENDER,
    CYBER
}

private fun oceanColors(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = OceanDarkPrimary,
            onPrimary = OceanDarkOnPrimary,
            secondary = OceanDarkSecondary,
            background = OceanDarkBackground,
            surface = OceanDarkSurface
        )
    } else {
        lightColorScheme(
            primary = OceanLightPrimary,
            onPrimary = OceanLightOnPrimary,
            secondary = OceanLightSecondary,
            background = OceanLightBackground,
            surface = OceanLightSurface
        )
    }

private fun natureColors(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = NatureDarkPrimary,
            onPrimary = NatureDarkOnPrimary,
            secondary = NatureDarkSecondary,
            background = NatureDarkBackground,
            surface = NatureDarkSurface
        )
    } else {
        lightColorScheme(
            primary = NatureLightPrimary,
            onPrimary = NatureLightOnPrimary,
            secondary = NatureLightSecondary,
            background = NatureLightBackground,
            surface = NatureLightSurface
        )
    }

private fun sunsetColors(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = SunsetDarkPrimary,
            onPrimary = SunsetDarkOnPrimary,
            secondary = SunsetDarkSecondary,
            background = SunsetDarkBackground,
            surface = SunsetDarkSurface
        )
    } else {
        lightColorScheme(
            primary = SunsetLightPrimary,
            onPrimary = SunsetLightOnPrimary,
            secondary = SunsetLightSecondary,
            background = SunsetLightBackground,
            surface = SunsetLightSurface
        )
    }

private fun lavenderColors(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = LavenderDarkPrimary,
            onPrimary = LavenderDarkOnPrimary,
            secondary = LavenderDarkSecondary,
            background = LavenderDarkBackground,
            surface = LavenderDarkSurface
        )
    } else {
        lightColorScheme(
            primary = LavenderLightPrimary,
            onPrimary = LavenderLightOnPrimary,
            secondary = LavenderLightSecondary,
            background = LavenderLightBackground,
            surface = LavenderLightSurface
        )
    }

private fun cyberColors(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = CyberDarkPrimary,
            onPrimary = CyberDarkOnPrimary,
            secondary = CyberDarkSecondary,
            background = CyberDarkBackground,
            surface = CyberDarkSurface
        )
    } else {
        lightColorScheme(
            primary = CyberLightPrimary,
            onPrimary = CyberLightOnPrimary,
            secondary = CyberLightSecondary,
            background = CyberLightBackground,
            surface = CyberLightSurface
        )
    }

fun AppTheme.colors(darkTheme: Boolean): ColorScheme =
    when (this) {
        AppTheme.OCEAN -> oceanColors(darkTheme)
        AppTheme.NATURE -> natureColors(darkTheme)
        AppTheme.SUNSET -> sunsetColors(darkTheme)
        AppTheme.LAVENDER -> lavenderColors(darkTheme)
        AppTheme.CYBER -> cyberColors(darkTheme)
    }

@Composable
fun MyAppTheme(
    theme: AppTheme = AppTheme.OCEAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = theme.colors(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}