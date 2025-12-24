package com.example.warning.presentation.ui.screens

import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.warning.domain.model.Profile
import com.example.warning.domain.model.Stats
import com.example.warning.domain.usecase.EmergencyState
import com.example.warning.presentation.ui.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    profile: Profile?,
    contactCount: Int,
    stats: Stats,
    emergencyState: EmergencyState,
    onNotificationsClick: () -> Unit,
    onContactsClick: () -> Unit,
    onDrawerDestinationClick: (String) -> Unit,
    onEmergencyClick: () -> Unit,
    onEmergencyDialogDismiss: () -> Unit
) {
    var isDrawerOpen by remember { mutableStateOf(false) }
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

    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background,
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 520f, gradientOffset + 520f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                IconButton(onClick = onNotificationsClick) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp,
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            IconButton(onClick = { isDrawerOpen = true }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedBrush)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 18.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Acil Durum",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(
                                text = "Konumunuzu ve kayıtlı kişilerinizi kullanarak tek dokunuşla acil durum bildirimi gönderin.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.size(18.dp))

                            PremiumEmergencyButton(onClick = onEmergencyClick)
                        }
                    }
                    Spacer(modifier = Modifier.size(22.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 12.dp
                    ) {
                        var isExpanded by rememberSaveable { mutableStateOf(true) }
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { isExpanded = !isExpanded }
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Hazırlık Durumu",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (isExpanded) {
                                PremiumStatusRow(
                                    title = "Konum",
                                    subtitle = if (hasLocationPermission && isLocationEnabled) "Açık" else if (!hasLocationPermission) "İzin yok" else "Kapalı",
                                    isReady = hasLocationPermission && isLocationEnabled,
                                    actionText = if (hasLocationPermission && isLocationEnabled) "Hazır" else "Kontrol Et",
                                    onActionClick = {
                                        if (!hasLocationPermission) {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        } else {
                                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                            context.startActivity(intent)
                                        }
                                    }
                                )

                                PremiumStatusRow(
                                    title = "Kişiler",
                                    subtitle = if (contactCount > 0) "Hazır" else "Eksik",
                                    isReady = contactCount > 0,
                                    actionText = if (contactCount > 0) "${contactCount} kayıtlı" else "Kişi Ekle",
                                    onActionClick = onContactsClick
                                )
                            }

                            Text(
                                text = "İpucu: Acil mesajın hızlı gitmesi için konum açık ve en az 1 kişi kayıtlı olmalı.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(22.dp))

                    // İstatistikler Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "İstatistikler",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    icon = Icons.AutoMirrored.Filled.Send,
                                    value = stats.sentCount.toString(),
                                    label = "Gönderilen",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    icon = Icons.AutoMirrored.Filled.CallReceived,
                                    value = stats.receivedCount.toString(),
                                    label = "Alınan",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatCard(
                                    icon = Icons.Default.Group,
                                    value = "$contactCount",
                                    label = "Bağlantı",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    icon = Icons.Default.Schedule,
                                    value = stats.lastMessageTime,
                                    label = "Son Mesaj",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(22.dp))

                    // Hızlı İşlemler
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 12.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Hızlı İşlemler",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickActionButton(
                                    icon = Icons.AutoMirrored.Filled.List,
                                    text = "Mesaj Geçmişi",
                                    onClick = { onDrawerDestinationClick(Routes.EMERGENCY_HISTORY) },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickActionButton(
                                    icon = Icons.Default.Person,
                                    text = "Kişilerim",
                                    onClick = onContactsClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isDrawerOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            RightSideDrawerContent(
                profile = profile,
                onDestinationClick = onDrawerDestinationClick,
                onClose = { isDrawerOpen = false }
            )
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

private fun Modifier.glow(
    color: Color,
    blurRadius: Dp = 16.dp,
): Modifier = this.then(
    Modifier.drawBehind {
        drawCircle(
            color = color.copy(alpha = 0.22f),
            radius = (size.minDimension / 2f) + blurRadius.toPx(),
            center = center
        )
    }
)

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    shadowElevation: Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.04f),
                        )
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
private fun PremiumStatusRow(
    title: String,
    subtitle: String,
    isReady: Boolean,
    actionText: String,
    onActionClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "status_row_scale",
    )

    val icon = if (isReady) Icons.Filled.CheckCircle else Icons.Filled.Close
    val iconContainer = if (isReady) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
    val iconTint = if (isReady) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            tryAwaitRelease()
                        } finally {
                            pressed = false
                        }
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(shape = CircleShape, color = iconContainer) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            AssistChip(
                onClick = onActionClick,
                label = { Text(actionText) },
            )
        }
    }
}

@Composable
private fun FloatingParticles(
    color: Color,
    count: Int = 8,
    baseRadius: Float = 64f,
    spread: Float = 24f,
) {
    repeat(count) { index ->
        val angle = (index * (360f / count)) * (PI.toFloat() / 180f)
        val t = rememberInfiniteTransition()
        val radius by t.animateFloat(
            initialValue = baseRadius,
            targetValue = baseRadius + spread,
            animationSpec = infiniteRepeatable(
                animation = tween(2000 + index * 180, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particle_radius_$index"
        )

        val x = cos(angle) * radius
        val y = sin(angle) * radius
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = x
                    translationY = y
                }
                .size(5.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.45f))
        )
    }
}

@Composable
private fun PremiumEmergencyButton(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val offsetY by animateFloatAsState(
        targetValue = if (pressed) 8f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "emergency_offset",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "emergency_scale",
    )

    val coreBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
        )
    )

    Box(contentAlignment = Alignment.Center) {
        FloatingParticles(color = MaterialTheme.colorScheme.primary)

        Box(
            modifier = Modifier
                .size(132.dp)
                .graphicsLayer {
                    translationY = offsetY
                    scaleX = scale
                    scaleY = scale
                }
                .glow(MaterialTheme.colorScheme.primary)
                .shadow(
                    elevation = (26 - offsetY * 1.6f).dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                )
                .clip(CircleShape)
                .background(coreBrush)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            val released = tryAwaitRelease()
                            pressed = false
                            if (released) {
                                onClick()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Acil",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

@Composable
private fun RightSideDrawerContent(
    profile: Profile?,
    onDestinationClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 280.dp, max = 320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {},
        shape = MaterialTheme.shapes.extraLarge.copy(
            topEnd = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                if (profile?.profilePhoto.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = profile?.profilePhoto),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column {
                    Text(
                        text = profile?.name ?: "Kullanıcı",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Profili Düzenle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onClose()
                            onDestinationClick(Routes.PROFILE)
                        }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.size(16.dp))

            DrawerMenuItem(
                label = "Profil",
                icon = Icons.Outlined.Person,
                onClick = {
                    onClose()
                    onDestinationClick(Routes.PROFILE)
                }
            )
            DrawerMenuItem(
                label = "Bağlantılarım",
                icon = Icons.Default.Face,
                onClick = {
                    onClose()
                    onDestinationClick(Routes.CONTACTS)
                }
            )
            DrawerMenuItem(
                label = "Acil Mesaj Geçmişi",
                icon = Icons.AutoMirrored.Filled.List,
                onClick = {
                    onClose()
                    onDestinationClick(Routes.EMERGENCY_HISTORY)
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            DrawerMenuItem(
                label = "Ayarlar",
                icon = Icons.Outlined.Settings,
                onClick = {
                    onClose()
                    onDestinationClick(Routes.SETTINGS)
                }
            )
        }
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        modifier = modifier
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        modifier = modifier
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
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}