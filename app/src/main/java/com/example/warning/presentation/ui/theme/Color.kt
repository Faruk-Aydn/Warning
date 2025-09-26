package com.example.warning.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// 🔴 Temel Renkler
val PrimaryRed = Color(0xFFF54927)      // Önemli butonlar, vurgular
val SecondaryGray = Color(0xFF8E8E93)   // Pasif yazılar, ikincil ikonlar
val InactiveRed = Color(0x4DFF3B30)     // Pasif hata, arka plan uyarıları
val ErrorRed = Color(0xFFFF3B30)        // Hata mesajları, yanlış giriş
val SuccessGreen = Color(0xFF4CD964)    // Başarılı işlem onayı
val BackgroundDark = Color(0xFF2C2C2E)  // Koyu arka plan

// 🟡 Ekstra Önerilen Renkler
val NeutralLight = Color(0xFFF2F2F7)    // Açık tema arka plan
val WarningYellow = Color(0xFFFFD60A)   // Uyarı durumları
val InfoBlue = Color(0xFF007AFF)        // Linkler, bilgi mesajları
val DividerGray = Color(0xFFC6C6C8)     // Ayırıcı çizgiler

// 🎨 Merkezi Renk Şeması
object AppColorScheme {
    // Temel
    val primary = PrimaryRed
    val secondary = SecondaryGray
    val inactive = InactiveRed
    val error = ErrorRed
    val success = SuccessGreen
    val backgroundDark = BackgroundDark

    // Ekstra
    val neutralLight = NeutralLight
    val warning = WarningYellow
    val info = InfoBlue
    val divider = DividerGray
    val successGreen =SuccessGreen
}

