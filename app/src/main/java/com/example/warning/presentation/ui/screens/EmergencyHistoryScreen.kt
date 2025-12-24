package com.example.warning.presentation.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.warning.domain.model.EmergencyMessage
import com.example.warning.presentation.viewModel.AuthViewModel
import com.example.warning.presentation.viewModel.EmergencyHistoryUiState
import com.example.warning.presentation.viewModel.EmergencyHistoryViewModel
import com.example.warning.presentation.viewModel.MessageFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHistoryScreen(
    navController: NavController,
    viewModel: EmergencyHistoryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var userId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val profile = authViewModel.isLoggedIn()
        userId = profile?.id ?: profile?.phoneNumber
        userId?.let { viewModel.loadHistory(it) }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition()
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.background,
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 500f, gradientOffset + 500f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            PremiumHistoryTopBar(
                messagesCount = uiState.messages.size,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    .padding(16.dp)
            ) {
                // Stats Card
                PremiumStatsCard(
                    totalCount = uiState.messages.size,
                    sentCount = uiState.messages.count { it.senderId == userId },
                    receivedCount = uiState.messages.count { it.senderId != userId }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Bar
                PremiumFilterBar(
                    selectedFilter = uiState.filter,
                    onFilterSelected = { viewModel.changeFilter(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                if (uiState.isLoading && uiState.messages.isEmpty()) {
                    PremiumLoadingState()
                } else if (uiState.messages.isEmpty()) {
                    PremiumEmptyState(filter = uiState.filter)
                } else {
                    PremiumHistoryList(
                        messages = uiState.messages,
                        onItemClick = { viewModel.onMessageClick(it) },
                        currentUserId = userId ?: ""
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumHistoryTopBar(
    messagesCount: Int,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Acil Mesaj Geçmişi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$messagesCount mesaj",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun PremiumStatsCard(
    totalCount: Int,
    sentCount: Int,
    receivedCount: Int
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.List,
                value = totalCount,
                label = "Toplam",
                color = MaterialTheme.colorScheme.primary
            )

            Divider(
                modifier = Modifier
                    .height(48.dp)
                    .width(1.dp)
            )

            StatItem(
                icon = Icons.Default.Send,
                value = sentCount,
                label = "Gönderilen",
                color = MaterialTheme.colorScheme.tertiary
            )

            Divider(
                modifier = Modifier
                    .height(48.dp)
                    .width(1.dp)
            )

            StatItem(
                icon = Icons.Default.CallReceived,
                value = receivedCount,
                label = "Alınan",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    label: String,
    color: Color
) {
    Column(
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
                modifier = Modifier.padding(10.dp)
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
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

@Composable
private fun PremiumFilterBar(
    selectedFilter: MessageFilter,
    onFilterSelected: (MessageFilter) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PremiumFilterChip(
                text = "Tümü",
                icon = Icons.Default.List,
                isSelected = selectedFilter == MessageFilter.ALL,
                onClick = { onFilterSelected(MessageFilter.ALL) },
                modifier = Modifier.weight(1f)
            )
            PremiumFilterChip(
                text = "Gönderilen",
                icon = Icons.Default.Send,
                isSelected = selectedFilter == MessageFilter.SENT,
                onClick = { onFilterSelected(MessageFilter.SENT) },
                modifier = Modifier.weight(1f)
            )
            PremiumFilterChip(
                text = "Alınan",
                icon = Icons.Default.CallReceived,
                isSelected = selectedFilter == MessageFilter.RECEIVED,
                onClick = { onFilterSelected(MessageFilter.RECEIVED) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PremiumFilterChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300)
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
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun PremiumHistoryList(
    messages: List<EmergencyMessage>,
    onItemClick: (EmergencyMessage) -> Unit,
    currentUserId: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages, key = { it.id ?: "" }) { message ->
            PremiumMessageCard(
                message = message,
                onClick = { onItemClick(message) },
                isSentByCurrentUser = message.senderId == currentUserId
            )
        }
    }
}

@Composable
private fun PremiumMessageCard(
    message: EmergencyMessage,
    onClick: () -> Unit,
    isSentByCurrentUser: Boolean
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
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Box {
            // Status indicator strip
            val statusColor = when {
                !message.isSuccess -> MaterialTheme.colorScheme.error
                isSentByCurrentUser -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                statusColor,
                                statusColor.copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with participants
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sender Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Direction Icon
                    Icon(
                        if (isSentByCurrentUser) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )

                    // Receiver Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.receiverName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (message.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (!message.isSuccess) "HATA" else message.status.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = statusColor
                            )
                        }
                    }
                }

                // Names
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " → ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message.receiverName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Message Content
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = message.messageContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Footer with timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = formatTimestamp(message.timestampMillis),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = "Geçmiş yükleniyor...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PremiumEmptyState(filter: MessageFilter) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 2.dp
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Text(
                text = when (filter) {
                    MessageFilter.ALL -> "Henüz mesaj yok"
                    MessageFilter.SENT -> "Gönderilen mesaj yok"
                    MessageFilter.RECEIVED -> "Alınan mesaj yok"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Acil durum mesajları gönderdiğinizde veya aldığınızda burada görünecektir",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
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

private fun formatTimestamp(timestampMillis: Long): String {
    return try {
        val instant = java.time.Instant.ofEpochMilli(timestampMillis)
        val zoneId = java.time.ZoneId.systemDefault()
        val dateTime = java.time.ZonedDateTime.ofInstant(instant, zoneId)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        dateTime.format(formatter)
    } catch (e: Exception) {
        timestampMillis.toString()
    }
}