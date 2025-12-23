package com.example.warning.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: com.example.warning.presentation.viewModel.SettingsViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState(initial = false)
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    var areNotificationsEnabled by remember {
        mutableStateOf(
            androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
        )
    }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                areNotificationsEnabled = androidx.core.app.NotificationManagerCompat.from(context)
                    .areNotificationsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        areNotificationsEnabled = isGranted
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background,
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 600f, gradientOffset + 600f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            PremiumSettingsTopBar()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Section
                PremiumSettingsHero()

                // Preferences Section
                Text(
                    text = "Tercihler",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )

                PremiumSettingSwitch(
                    icon = Icons.Default.Notifications,
                    title = "Bildirimler",
                    description = "Acil durum ve bağlantı bildirimlerini al",
                    checked = areNotificationsEnabled,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    onCheckedChange = { checked ->
                        if (checked) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.openAppSystemSettings()
                            }
                        } else {
                            viewModel.openAppSystemSettings()
                        }
                    }
                )

                PremiumSettingSwitch(
                    icon = Icons.Default.DarkMode,
                    title = "Koyu Tema",
                    description = "Karanlık mod deneyimi",
                    checked = isDarkTheme,
                    iconColor = MaterialTheme.colorScheme.secondary,
                    onCheckedChange = { viewModel.toggleTheme(it) }
                )

                PremiumSettingSwitch(
                    icon = Icons.Default.LocationOn,
                    title = "Konum İzni",
                    description = "Acil durum için konum izni",
                    checked = hasLocationPermission,
                    iconColor = MaterialTheme.colorScheme.primary,
                    onCheckedChange = { checked ->
                        if (checked) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            viewModel.openAppSystemSettings()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Privacy Section
                Text(
                    text = "Güvenlik",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )

                PremiumSettingCard(
                    icon = Icons.Default.Security,
                    title = "Gizlilik ve Güvenlik",
                    description = "Veri kullanımı ve izin ayarlarını görüntüle",
                    onClick = { viewModel.openAppSystemSettings() }
                )

                PremiumSettingCard(
                    icon = Icons.Default.Info,
                    title = "Uygulama Bilgisi",
                    description = "Versiyon 1.0.0 • Build 2024",
                    onClick = { /* Açıklama dialog'u */ }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Logout Button
                PremiumLogoutButton(
                    onClick = { viewModel.logout(onLogout) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumSettingsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Ayarlar",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun PremiumSettingsHero() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Icon
            AnimatedSettingsIcon()

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ayarları Yönet",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Uygulamanızı özelleştirin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimatedSettingsIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating gradient ring
        Box(
            modifier = Modifier
                .size(60.dp)
                .graphicsLayer { rotationZ = rotation }
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.primary,
                        )
                    ),
                    CircleShape
                )
        )

        // Inner icon
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumSettingSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    iconColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Container
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                tonalElevation = 2.dp
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Animated Switch
            AnimatedSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val thumbColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300)
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300)
    )

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            uncheckedThumbColor = thumbColor,
            uncheckedTrackColor = trackColor
        )
    )
}

@Composable
private fun PremiumSettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Container
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Arrow Icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun PremiumLogoutButton(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { showDialog = true }
                )
            },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 12.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.errorContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError
                )
                Text(
                    text = "Çıkış Yap",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    tonalElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            title = {
                Text(
                    "Çıkış Yap",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    "Hesabınızdan çıkış yapmak istediğinize emin misiniz?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Çıkış Yap")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    shadowElevation: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f),
                        )
                    )
                )
        ) {
            content()
        }
    }
}