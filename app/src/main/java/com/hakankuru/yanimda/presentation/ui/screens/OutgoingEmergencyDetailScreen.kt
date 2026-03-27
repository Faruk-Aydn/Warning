package com.hakankuru.yanimda.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hakankuru.yanimda.domain.model.EmergencyMessage
import com.hakankuru.yanimda.presentation.viewModel.OutgoingDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutgoingEmergencyDetailScreen(
    navController: NavController,
    viewModel: OutgoingDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animated gradient background
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
            TopAppBar(
                title = {
                    Text(
                        text = "Gönderilen Mesaj Detayı",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 3.dp,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBrush)
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.message != null) {
                val message = uiState.message!!
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutgoingDetailCard(message = message)
                }
            }
        }
    }
}

@Composable
private fun OutgoingDetailCard(message: EmergencyMessage) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
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
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header (Status)
                val statusColor = if (message.isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "İşlem Durumu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (message.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                            val statusText = when (message.status.name) {
                                "DELIVERED" -> "İLETİLDİ"
                                "SENT" -> "GÖNDERİLDİ"
                                "FAILED" -> "HATA"
                                else -> message.status.name
                            }
                            Text(
                                text = if (!message.isSuccess) "HATA" else statusText,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Receiver Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Alıcı",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = message.receiverName,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Receiver Number / ID
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Alıcı Numara",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val numText = if (message.receiverCountry != null && message.receiverPhone != null) {
                            "+${message.receiverCountry} ${message.receiverPhone}"
                        } else {
                            message.receiverId
                        }
                        Text(
                            text = numText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tarih",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTimestampDetailOut(message.timestampMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Message Content
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Mesajİçeriği:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = message.messageContent,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                // Location Sent Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (message.hasLocation) Icons.Default.LocationOn else Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = if (message.hasLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Konum Bilgisi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (message.hasLocation) "Gönderildi" else "Gönderilmedi",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestampDetailOut(timestampMillis: Long): String {
    return try {
        val instant = java.time.Instant.ofEpochMilli(timestampMillis)
        val zoneId = java.time.ZoneId.systemDefault()
        val dateTime = java.time.ZonedDateTime.ofInstant(instant, zoneId)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        dateTime.format(formatter)
    } catch (e: Exception) {
        timestampMillis.toString()
    }
}
