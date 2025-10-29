package com.example.warning.presentation.ui.screens


import android.R.attr.contentDescription
import android.R.attr.phoneNumber
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import com.example.warning.presentation.viewModel.EmergencyMessageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import com.example.warning.domain.usecase.EmergencyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: ProfileListenerViewModel= hiltViewModel(),
    contactVm: ContactListenerViewmodel = hiltViewModel(),
    emergencyViewModel: EmergencyMessageViewModel = hiltViewModel()
) {
    // Drawer kontrolü için
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Konum izin kontrolü
    fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    // Konum servisi kontrolü
    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // İlk izin kontrolü
    var hasLocationPermission by remember { 
        mutableStateOf(checkLocationPermission()) 
    }
    
    // Konum servisi durumu
    var isLocationEnabled by remember { 
        mutableStateOf(isLocationServiceEnabled()) 
    }
    
    var isLocationTransition by remember { mutableStateOf(false) }

    // İzin isteme launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermission = fineLocationGranted || coarseLocationGranted
        
        // İzin verildiyse konum servisi durumunu kontrol et
        if (hasLocationPermission) {
            isLocationEnabled = isLocationServiceEnabled()
        }
    }

    // Konum servisi durumunu periyodik kontrol et (compose lifecycle içinde)
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            // İzin varsa, konum servisi durumunu kontrol et
            while (true) {
                isLocationEnabled = isLocationServiceEnabled()
                delay(1000) // Her saniye kontrol et
            }
        }
    }

    val profile by viewModel.profileState.collectAsState()
    val contacts by contactVm.contacts.collectAsState()
    val emergencyState by emergencyViewModel.emergencyMessageState.collectAsState()

    // Bağlantı sayısı (örnek 15)
    var contactCount by remember { mutableStateOf(contacts.size) }

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

                                navController.navigate("profile")
                            }) {
                                if (profile?.profilePhoto.isNullOrEmpty()) {
                                // Null durumda default ikon
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(40.dp) // boyutunu ayarlayabilirsin
                                )
                            } else {
                                    val painter = rememberAsyncImagePainter(model = profile?.profilePhoto)
                                    val state = painter.state

                                    Box(modifier = Modifier.size(40.dp)) {
                                        when (state) {
                                            is AsyncImagePainter.State.Loading -> {
                                                // Yüklenirken progress göster
                                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.matchParentSize())
                                            }
                                            is AsyncImagePainter.State.Error -> {
                                                // Hata durumunda ikon göster
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = "Profile",
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            }
                                            else -> {
                                                // Başarılı durumda resmi göster
                                                Image(
                                                    painter = painter,
                                                    contentDescription = "Profile Image",
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .clip(CircleShape)
                                                )
                                            }
                                        }
                                    }
                            }
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
                    onClick = { 
                        emergencyViewModel.sendEmergencyMessage()
                    }
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
                                navController.navigate(route = Routes.Contacts)
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
                                    // İzin yoksa izin iste
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                } else {
                                    // İzin varsa her tıklamada konum ayarlarını aç
                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    context.startActivity(intent)
                                    isLocationTransition = true
                                    scope.launch {
                                        delay(400)
                                        isLocationTransition = false
                                    }
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

    // Emergency message state'e göre dialog göster
    val currentEmergencyState = emergencyState
    when (currentEmergencyState) {
        is EmergencyState.Loading -> {
            // Loading dialog
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Acil Durum Mesajı Gönderiliyor") },
                text = { 
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = { }
            )
        }
        is EmergencyState.Success -> {
            // Success dialog
            AlertDialog(
                onDismissRequest = { emergencyViewModel.resetState() },
                title = { Text("Başarılı") },
                text = { 
                    Text("${currentEmergencyState.successCount} kişiye acil durum mesajı gönderildi.\n" +
                            "${currentEmergencyState.failureCount} kişiye gönderilemedi"
                    )
                },
                confirmButton = {
                    Button(onClick = { emergencyViewModel.resetState() }) {
                        Text("Tamam")
                    }
                }
            )
        }
        is EmergencyState.Error -> {
            // Error dialog
            AlertDialog(
                onDismissRequest = { emergencyViewModel.resetState() },
                title = { Text("Hata") },
                text = { Text(currentEmergencyState.message) },
                confirmButton = {
                    Button(onClick = { emergencyViewModel.resetState() }) {
                        Text("Tamam")
                    }
                }
            )
        }
        else -> { /* Idle state - dialog gösterme */ }
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
