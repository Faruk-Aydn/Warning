package com.example.warning.presentation.ui.screens

import com.example.warning.presentation.ui.theme.AppColorScheme

// - NavHostController : androidx.navigation.NavHostController (Navigation Compose)
// - LaunchedEffect, delay  : compose runtime coroutine scope
// - CircularProgressIndicator : androidx.compose.material3.CircularProgressIndicator (Compose Material3)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.compose.material3.CircularProgressIndicator

// Basit route sabitleri (NavGraph'teki route isimleri ile eşleşmeli)
object Routes {
    const val Splash = "splash"
    const val Main = "main"
    const val SignIn = "sign_in"
}

/**
 * SplashScreen
 *
 * - Arka plan: AppColorScheme.BackgroundDark
 * - Ortadaki küçük ilerleme halkası: AppColorScheme.PrimaryRed
 * - Açıldığında kısa süre görünür; sonra kullanıcı durumuna göre yönlendirir.
 *
 * NOT: Bu composable sadece arayüzü gösterir ve yönlendirme tetikler.
 * Gerçek "kullanıcı giriş kontrolü" (Room / DataStore / Firebase / repo çağrıları)
 * ViewModel / Repository içinde yapılmalı. Aşağıdaki LaunchedEffect içindeki
 * `isLoggedIn` değişkeni demo amaçlıdır — bunu kendi ViewModel'inizden / repo'dan alın.
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorScheme.backgroundDark), // merkezi renk dosyanızdan alınıyor
        contentAlignment = Alignment.Center
    ) {
        // Küçük bir CircularProgressIndicator (yükleniyor göstergesi)
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp), // küçük boyut
            color = AppColorScheme.primary, // merkezi renk dosyanızdan alınıyor
            strokeWidth = 3.dp
        )
    }

    // Geçiş mantığı (kısa süre sonra yönlendirir)
    LaunchedEffect(Unit) {
        // ---------- Burada GERÇEK kontrolü yapmalısınız ----------
        // Önerilen uygulama (mimari):
        // 1) ViewModel (AuthViewModel gibi) oluşturun.
        // 2) ViewModel, Repository / DataStore / Room / Firebase'den isLoggedIn durumunu expose etsin (Flow veya LiveData).
        // 3) Bu composable içinde ViewModel'i observe edin (collectAsState / observeAsState) ve o değere göre navigate edin.
        //
        // Örnek (pseudocode, gerçek kod ViewModel tarafında olmalı):
        // val isLoggedIn = authViewModel.isLoggedInFlow.collectAsState(initial = null)
        // LaunchedEffect(isLoggedIn.value) { ... navigate ... }
        //
        // ---------- Bu demo için kısa bir bekleme koyuyoruz (UI efekt) ----------
        delay(700L) // splash'in kısa süre görünmesi için: 700ms. Gerçek projede burayı ayarlayın.

        // ----- DEMO: placeholder -----
        // Bu satırı gerçek kontrol ile değiştirin (ör. authRepository.isUserLoggedIn() suspend çağrısı)
        val isLoggedIn = false // TODO: Replace with real check from ViewModel/Repository

        // ----- Yönlendirme -----
        if (isLoggedIn) {
            // Eğer giriş yapılmışsa Main ekranına git.
            // popUpTo ile splash'i backstack'ten kaldırıyoruz, böylece geri tuşu splash'e dönmez.
            navController.navigate(Routes.Main) {
                launchSingleTop = true
                popUpTo(Routes.Splash) { inclusive = true }
            }
        } else {
            // Giriş yapılmamışsa SignIn ekranına yönlendir.
            navController.navigate(Routes.SignIn) {
                launchSingleTop = true
                popUpTo(Routes.Splash) { inclusive = true }
            }
        }
    }
}