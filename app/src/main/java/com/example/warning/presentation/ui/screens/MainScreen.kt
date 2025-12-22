package com.example.warning.presentation.ui.screens

import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.warning.domain.usecase.EmergencyState
import androidx.compose.ui.graphics.Color
import com.example.warning.presentation.ui.theme.AppColorScheme
import com.example.warning.domain.model.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.warning.presentation.ui.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    profile: Profile?,
    contactCount: Int,
    emergencyState: EmergencyState,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onContactsClick: () -> Unit,
    onDrawerDestinationClick: (String) -> Unit,
    onEmergencyClick: () -> Unit,
    onEmergencyDialogDismiss: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    var hasLocationPermission by remember {
        mutableStateOf(checkLocationPermission())
    }

    var isLocationEnabled by remember {
        mutableStateOf(isLocationServiceEnabled())
    }

    var isLocationTransition by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted =
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted =
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermission = fineLocationGranted || coarseLocationGranted

        if (hasLocationPermission) {
            isLocationEnabled = isLocationServiceEnabled()
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            while (true) {
                isLocationEnabled = isLocationServiceEnabled()
                delay(1000)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                drawerState = drawerState,
                onDestinationClick = { route -> onDrawerDestinationClick(route) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        Row {
                            IconButton(onClick = {
                                onProfileClick()
                            }) {
                                if (profile?.profilePhoto.isNullOrEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    val painter =
                                        rememberAsyncImagePainter(model = profile?.profilePhoto)
                                    val state = painter.state

                                    Box(modifier = Modifier.size(40.dp)) {
                                        when (state) {
                                            is AsyncImagePainter.State.Loading -> {
                                                CircularProgressIndicator(
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            }

                                            is AsyncImagePainter.State.Error -> {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = "Profile",
                                                    modifier = Modifier.matchParentSize()
                                                )
                                            }

                                            else -> {
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
                                onNotificationsClick()
                            }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Acil Durum",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        text = "Konumunuzu ve kayıtlı kişilerinizi kullanarak tek dokunuşla acil durum bildirimi gönderin.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.size(32.dp))

                    AnimatedCircleButton(onClick = onEmergencyClick)
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onContactsClick() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Contacts",
                                tint = AppColorScheme.successGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "$contactCount bağlantı",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    if (!hasLocationPermission) {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    } else {
                                        val intent =
                                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
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
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                "Konum",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    val currentEmergencyState = emergencyState
    when (currentEmergencyState) {
        is EmergencyState.Loading -> {
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
            AlertDialog(
                onDismissRequest = onEmergencyDialogDismiss,
                title = { Text("Başarılı") },
                text = {
                    Text(
                        "${currentEmergencyState.successCount} kişiye acil durum mesajı gönderildi.\n" +
                                "${currentEmergencyState.failureCount} kişiye gönderilemedi"
                    )
                },
                confirmButton = {
                    Button(onClick = onEmergencyDialogDismiss) {
                        Text("Tamam")
                    }
                }
            )
        }

        is EmergencyState.Error -> {
            AlertDialog(
                onDismissRequest = onEmergencyDialogDismiss,
                title = { Text("Hata") },
                text = { Text(currentEmergencyState.message) },
                confirmButton = {
                    Button(onClick = onEmergencyDialogDismiss) {
                        Text("Tamam")
                    }
                }
            )
        }

        EmergencyState.Idle -> Unit
    }
}

@Composable
private fun AnimatedCircleButton(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000,
                        delayMillis = index * 1000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000,
                        delayMillis = index * 1000,
                        easing = LinearEasing
                    ),
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

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AppColorScheme.primary)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Main Button",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
@Composable
private fun DrawerContent(
    drawerState: DrawerState,
    onDestinationClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        DrawerItem("Profil", Routes.PROFILE, drawerState, onDestinationClick)
        DrawerItem("Bağlantılarım", Routes.CONTACTS, drawerState, onDestinationClick)
        DrawerItem("Ayarlar", Routes.SETTINGS, drawerState, onDestinationClick)
        DrawerItem("Acil Mesaj Geçmişi", Routes.EMERGENCY_HISTORY, drawerState, onDestinationClick)
    }
}

@Composable
private fun DrawerItem(
    label: String,
    route: String,
    drawerState: DrawerState,
    onDestinationClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = {
            scope.launch { drawerState.close() }
            onDestinationClick(route)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
