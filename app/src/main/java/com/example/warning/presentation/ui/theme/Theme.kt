package com.example.warning.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppColorScheme.primary,
    onPrimary = AppColorScheme.onPrimary,
    primaryContainer = AppColorScheme.primaryContainerDark,
    onPrimaryContainer = AppColorScheme.onPrimaryContainerDark,
    secondary = AppColorScheme.secondaryDark,
    onSecondary = AppColorScheme.onSecondaryDark,
    secondaryContainer = AppColorScheme.secondaryContainerDark,
    onSecondaryContainer = AppColorScheme.onSecondaryContainerDark,
    tertiary = AppColorScheme.tertiaryDark,
    onTertiary = AppColorScheme.onTertiaryDark,
    tertiaryContainer = AppColorScheme.tertiaryContainerDark,
    onTertiaryContainer = AppColorScheme.onTertiaryContainerDark,
    background = AppColorScheme.backgroundDark,
    onBackground = AppColorScheme.onBackgroundDark,
    surface = AppColorScheme.surfaceDark,
    onSurface = AppColorScheme.onSurfaceDark,
    surfaceVariant = AppColorScheme.surfaceVariantDark,
    onSurfaceVariant = AppColorScheme.onSurfaceVariantDark,
    outline = AppColorScheme.outlineDark,
    error = AppColorScheme.error,
    onError = AppColorScheme.onError,
    errorContainer = AppColorScheme.errorContainerDark,
    onErrorContainer = AppColorScheme.onErrorContainerDark,
)

private val LightColorScheme = lightColorScheme(
    primary = AppColorScheme.primary,
    onPrimary = AppColorScheme.onPrimary,
    primaryContainer = AppColorScheme.primaryContainerLight,
    onPrimaryContainer = AppColorScheme.onPrimaryContainerLight,
    secondary = AppColorScheme.secondaryLight,
    onSecondary = AppColorScheme.onSecondaryLight,
    secondaryContainer = AppColorScheme.secondaryContainerLight,
    onSecondaryContainer = AppColorScheme.onSecondaryContainerLight,
    tertiary = AppColorScheme.tertiaryLight,
    onTertiary = AppColorScheme.onTertiaryLight,
    tertiaryContainer = AppColorScheme.tertiaryContainerLight,
    onTertiaryContainer = AppColorScheme.onTertiaryContainerLight,
    background = AppColorScheme.backgroundLight,
    onBackground = AppColorScheme.onBackgroundLight,
    surface = AppColorScheme.surfaceLight,
    onSurface = AppColorScheme.onSurfaceLight,
    surfaceVariant = AppColorScheme.surfaceVariantLight,
    onSurfaceVariant = AppColorScheme.onSurfaceVariantLight,
    outline = AppColorScheme.outlineLight,
    error = AppColorScheme.error,
    onError = AppColorScheme.onError,
    errorContainer = AppColorScheme.errorContainerLight,
    onErrorContainer = AppColorScheme.onErrorContainerLight,
)

@Composable
fun WarningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = WarningShapes,
        content = content
    )
}