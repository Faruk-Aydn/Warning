package com.hakankuru.yanimda.presentation.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hakankuru.yanimda.presentation.viewModel.ProfileScreenViewModel
import com.hakankuru.yanimda.presentation.viewModel.ProfileUiState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    navController: NavController,
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ProfileScreen(navController = navController, uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    uiState: ProfileUiState,
) {
    val profile = uiState.profile
    val contacts = uiState.contacts
    val linked = uiState.linked

    // Animasyonlu gradient arka plan
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val animatedBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
            )
        },
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 1200f, gradientOffset + 1200f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profil",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush)
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (profile != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Premium Profil Header
                    item {
                        PremiumProfileHeader(
                            name = profile.name ?: "İsimsiz",
                            country = profile.country,
                            phone = profile.phoneNumber
                        )
                    }

                    // 2. İzinler - Glassmorphism Style
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumPermissionBadge(
                                    isGranted = uiState.isLocationPermissionGranted,
                                    icon = Icons.Default.LocationOn,
                                    text = "Konum"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                PremiumPermissionBadge(
                                    isGranted = uiState.isContactsPermissionGranted,
                                    icon = Icons.Default.AccountBox,
                                    text = "Kişiler"
                                )
                            }
                        }
                    }

                    // 3. Acil Durum Mesajı
                    profile.emergencyMessage?.let { msg ->
                        item {
                            PremiumEmergencyCard(message = msg)
                        }
                    }

                    // 4. Bağlı Hesaplar
                    if (linked.isNotEmpty()) {
                        item {
                            PremiumSectionTitle(
                                title = "Bağlı Hesaplar (${linked.size})",
                                icon = Icons.Default.Share
                            )
                        }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(linked) { link ->
                                    PremiumLinkedAccountCard(phone = link.phoneNumber)
                                }
                            }
                        }
                    }

                    // 5. Kişiler
                    if (contacts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            PremiumSectionTitle(
                                title = "Kişiler (${contacts.size})",
                                icon = Icons.Default.Person
                            )
                        }
                        items(contacts) { contact ->
                            PremiumContactItem(
                                name = contact.name,
                                phone = contact.phoneNumber
                            )
                        }
                    } else {
                        item {
                            PremiumEmptyState(text = "Kişi listesi boş")
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PremiumEmptyState(text = "Profil bilgisi bulunamadı")
                }
            }
        }
    }
}

// Premium Components

@Composable
private fun PremiumProfileHeader(name: String, country: String, phone: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Profile Circle
            AnimatedProfileCircle(initial = name.take(1).uppercase())

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoChip(icon = Icons.Default.Home, text = country)
                InfoChip(icon = Icons.Default.Phone, text = phone)
            }
        }
    }
}

@Composable
private fun AnimatedProfileCircle(initial: String) {
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
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating gradient ring
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer { rotationZ = rotation }
                .background(
                    Brush.sweepGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.primary,
                        )
                    ),
                    CircleShape
                )
        )

        // Inner circle
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PremiumPermissionBadge(
    isGranted: Boolean,
    icon: ImageVector,
    text: String
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val containerColor = if (isGranted) {
        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
    }
    val contentColor = if (isGranted) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    val statusIcon = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Close

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
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(statusIcon, null, tint = contentColor, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun PremiumEmergencyCard(message: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                tonalElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.Info,
                    null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column {
                Text(
                    "Acil Durum Mesajı",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun PremiumSectionTitle(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            tonalElevation = 2.dp
        ) {
            Icon(
                icon,
                null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PremiumLinkedAccountCard(phone: String) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    GlassCard(
        modifier = Modifier
            .width(180.dp)
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
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                tonalElevation = 2.dp
            ) {
                Icon(
                    Icons.Default.Share,
                    null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Bağlı Hesap",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PremiumContactItem(name: String, phone: String) {
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
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar with primary scheme styling
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PremiumEmptyState(text: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

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
        color = Color.Transparent, // Tamamen şeffaf taban
        tonalElevation = 0.dp,
        shadowElevation = 0.dp // Etrafa gölge yayılmasını önlemek için 0
    ) {
        Box(
            modifier = Modifier
                .clip(shape) // İçindeki gradient arka planı da aynı köşelere yuvarlar
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    shape = shape
                )
        ) {
            content()
        }
    }
}