package com.example.warning.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// Temel marka renkleri (Warning / acil durum odaklı)
val PrimaryRed = Color(0xFFF54927)
val SecondaryGray = Color(0xFF64748B)
val InactiveRed = Color(0x4DFF3B30)
val ErrorRed = Color(0xFFFF3B30)
val SuccessGreen = Color(0xFF4CD964)

// Arka plan ve yüzeyler
val BackgroundDark = Color(0xFF0B1020)
val SurfaceDark = Color(0xFF111827)
val OnBackgroundDark = Color(0xFFE5E7EB)
val OnSurfaceDark = Color(0xFFE5E7EB)

val NeutralLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF0F172A)
val OnSurfaceLight = Color(0xFF0F172A)

// Durum renkleri
val WarningYellow = Color(0xFFFFD60A)
val InfoBlue = Color(0xFF007AFF)
val DividerGray = Color(0xFFC6C6C8)
val OutlineDark = Color(0xFF334155)
val OutlineLight = Color(0xFFCBD5E1)

val PrimaryContainerLight = Color(0xFFFFE4DF)
val OnPrimaryContainerLight = Color(0xFF3A0B05)
val PrimaryContainerDark = Color(0xFF5B1A11)
val OnPrimaryContainerDark = Color(0xFFFFDAD3)

val SecondaryLight = Color(0xFF475569)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE2E8F0)
val OnSecondaryContainerLight = Color(0xFF0F172A)

val SecondaryDark = Color(0xFF94A3B8)
val OnSecondaryDark = Color(0xFF0B1020)
val SecondaryContainerDark = Color(0xFF1E293B)
val OnSecondaryContainerDark = Color(0xFFE2E8F0)

val TertiaryLight = Color(0xFF0F766E)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFCCFBF1)
val OnTertiaryContainerLight = Color(0xFF022C22)

val TertiaryDark = Color(0xFF5EEAD4)
val OnTertiaryDark = Color(0xFF022C22)
val TertiaryContainerDark = Color(0xFF134E4A)
val OnTertiaryContainerDark = Color(0xFFCCFBF1)

val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF334155)
val SurfaceVariantDark = Color(0xFF1E293B)
val OnSurfaceVariantDark = Color(0xFFCBD5E1)

val ErrorContainerLight = Color(0xFFFFDAD4)
val OnErrorContainerLight = Color(0xFF410002)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD4)

// Uygulama içinde kullanılacak merkezi renk şeması
object AppColorScheme {
    // Brand
    val primary = PrimaryRed
    val onPrimary = Color.White
    val secondary = SecondaryGray

    val primaryContainerLight = PrimaryContainerLight
    val onPrimaryContainerLight = OnPrimaryContainerLight
    val primaryContainerDark = PrimaryContainerDark
    val onPrimaryContainerDark = OnPrimaryContainerDark

    val secondaryLight = SecondaryLight
    val onSecondaryLight = OnSecondaryLight
    val secondaryContainerLight = SecondaryContainerLight
    val onSecondaryContainerLight = OnSecondaryContainerLight

    val secondaryDark = SecondaryDark
    val onSecondaryDark = OnSecondaryDark
    val secondaryContainerDark = SecondaryContainerDark
    val onSecondaryContainerDark = OnSecondaryContainerDark

    val tertiaryLight = TertiaryLight
    val onTertiaryLight = OnTertiaryLight
    val tertiaryContainerLight = TertiaryContainerLight
    val onTertiaryContainerLight = OnTertiaryContainerLight

    val tertiaryDark = TertiaryDark
    val onTertiaryDark = OnTertiaryDark
    val tertiaryContainerDark = TertiaryContainerDark
    val onTertiaryContainerDark = OnTertiaryContainerDark

    val onError = Color.White
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
    val surfaceVariantDark = SurfaceVariantDark
    val onSurfaceVariantDark = OnSurfaceVariantDark
    val outlineDark = OutlineDark
    val errorContainerDark = ErrorContainerDark
    val onErrorContainerDark = OnErrorContainerDark

    // Light tema
    val neutralLight = NeutralLight
    val backgroundLight = NeutralLight
    val surfaceLight = SurfaceLight
    val onBackgroundLight = OnBackgroundLight
    val onSurfaceLight = OnSurfaceLight
    val surfaceVariantLight = SurfaceVariantLight
    val onSurfaceVariantLight = OnSurfaceVariantLight
    val outlineLight = OutlineLight
    val errorContainerLight = ErrorContainerLight
    val onErrorContainerLight = OnErrorContainerLight

    // Diğer
    val divider = DividerGray
    val successGreen = SuccessGreen
}
