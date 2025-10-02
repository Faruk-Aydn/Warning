package com.example.warning.presentation.ui.screens


import android.R.attr.phoneNumber
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: ProfileListenerViewModel= hiltViewModel()
) {
    // Drawer kontrolü için
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Konum izin & durum değerleri
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isLocationEnabled by remember { mutableStateOf(false) }
    var isLocationTransition by remember { mutableStateOf(false) }

    // Bağlantı sayısı (örnek 15)
    var contactCount by remember { mutableStateOf(15) }

    var triggerToggle by remember { mutableStateOf(false) }

    LaunchedEffect(triggerToggle) {
        if (hasLocationPermission) {
            delay(500) // bekleme
            isLocationEnabled = !isLocationEnabled
            isLocationTransition = false
        }
    }

    val profile by viewModel.profileState.collectAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController = navController, drawerState = drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        Row {
                            IconButton(onClick = {
                                // Profil ekranına git

                                navController.navigate("Profile")
                            }) {
                                Icon(Icons.Default.Person, contentDescription = "Profile")
                            }
                            IconButton(onClick = {
                                // Bildirim ekranına git
                                navController.navigate("NotifiScreen")
                            }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                // Ortadaki büyük yuvarlak buton + animasyonlu çemberler
                AnimatedCircleButton(
                    onClick = { /* TODO: ileride işlevi belirlenecek */ }
                )

                // Alt ikonlar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Sol: Kişiler
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 48.dp)
                            .clickable {
                                // Contact listesine git
                                navController.navigate("ContactListScreen")
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Contacts",
                            tint = AppColorScheme.successGreen, // sabit yeşil
                            modifier = Modifier.size(40.dp)
                        )
                        Text("$contactCount bağlantı")
                    }

                    // Sağ: Konum
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 48.dp)
                            .clickable {
                                // Tıklama ile durum güncelleniyor
                                if (!hasLocationPermission) {
                                    hasLocationPermission = true
                                } else {
                                    // Kısa animasyon bekleme
                                    isLocationTransition = true
                                    triggerToggle = !triggerToggle // sadece state değiştir
                                }
                            }
                    ) {
                        val color = when {
                            isLocationTransition -> AppColorScheme.neutralLight
                            !hasLocationPermission -> AppColorScheme.secondary
                            hasLocationPermission && !isLocationEnabled -> AppColorScheme.warning
                            hasLocationPermission && isLocationEnabled -> AppColorScheme.successGreen
                            else -> Color.Gray
                        }

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = color,
                            modifier = Modifier.size(40.dp)
                        )
                        Text("Konum")
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(navController: NavHostController, drawerState: DrawerState) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f) // sağdan çıkan menü
            .background(Color.White)
            .padding(16.dp)
    ) {
        TextButton(onClick = { navController.navigate("ProfileScreen") }) { Text("Profil") }
        TextButton(onClick = { navController.navigate("NotifiScreen") }) { Text("Bildirimler") }
        TextButton(onClick = { navController.navigate("ContactListScreen") }) { Text("Bağlantılarım") }
        TextButton(onClick = { navController.navigate("LinkedListScreen") }) { Text("Linked") }
        TextButton(onClick = { navController.navigate("RequestScreen") }) { Text("İstekler") }
        TextButton(onClick = { navController.navigate("SettingsScreen") }) { Text("Ayarlar") }
    }
}

@Composable
private fun AnimatedCircleButton(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        // Animasyonlu çemberler
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, delayMillis = index * 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, delayMillis = index * 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clip(CircleShape)
                    .background(AppColorScheme.primary)
            )
        }

        // Ortadaki buton
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AppColorScheme.primary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning, // şimdilik placeholder
                contentDescription = "Main Button",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
