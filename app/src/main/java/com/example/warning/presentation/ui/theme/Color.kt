package com.example.warning.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Temel marka renkleri (Warning / acil durum odaklı)
val PrimaryRed = Color(0xFFF54927)
val SecondaryGray = Color(0xFF8E8E93)
val InactiveRed = Color(0x4DFF3B30)
val ErrorRed = Color(0xFFFF3B30)
val SuccessGreen = Color(0xFF4CD964)

// Arka plan ve yüzeyler
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val OnBackgroundDark = Color(0xFFF5F5F7)
val OnSurfaceDark = Color(0xFFF5F5F7)

val NeutralLight = Color(0xFFF2F2F7)
val SurfaceLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF111111)
val OnSurfaceLight = Color(0xFF111111)

// Durum renkleri
val WarningYellow = Color(0xFFFFD60A)
val InfoBlue = Color(0xFF007AFF)
val DividerGray = Color(0xFFC6C6C8)

// Uygulama içinde kullanılacak merkezi renk şeması
object AppColorScheme {
    // Brand
    val primary = PrimaryRed
    val secondary = SecondaryGray
    val inactive = InactiveRed

    // Durum
    val error = ErrorRed
    val success = SuccessGreen
    val warning = WarningYellow
    val info = InfoBlue

    // Dark tema
    val backgroundDark = BackgroundDark
    val surfaceDark = SurfaceDark
    val onBackgroundDark = OnBackgroundDark
    val onSurfaceDark = OnSurfaceDark

    // Light tema
    val neutralLight = NeutralLight
    val surfaceLight = SurfaceLight
    val onBackgroundLight = OnBackgroundLight
    val onSurfaceLight = OnSurfaceLight

    // Diğer
    val divider = DividerGray
    val successGreen = SuccessGreen
}

