package com.example.warning.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppColorScheme.primary,
    onPrimary = Color.White,
    secondary = AppColorScheme.secondary,
    onSecondary = Color.White,
    background = AppColorScheme.backgroundDark,
    onBackground = AppColorScheme.onBackgroundDark,
    surface = AppColorScheme.surfaceDark,
    onSurface = AppColorScheme.onSurfaceDark,
    error = AppColorScheme.error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = AppColorScheme.primary,
    onPrimary = Color.White,
    secondary = AppColorScheme.secondary,
    onSecondary = Color.White,
    background = AppColorScheme.neutralLight,
    onBackground = AppColorScheme.onBackgroundLight,
    surface = AppColorScheme.surfaceLight,
    onSurface = AppColorScheme.onSurfaceLight,
    error = AppColorScheme.error,
    onError = Color.White
)

@Composable
fun WarningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = WarningShapes,
        content = content
    )
}