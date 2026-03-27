package com.hakankuru.yanimda.presentation.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.hakankuru.yanimda.presentation.viewModel.IncomingDetailViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomingEmergencyDetailScreen(
    navController: NavController,
    viewModel: IncomingDetailViewModel = hiltViewModel()
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
                        text = "Mesaj Detayı",
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
                    DetailCard(message = message)
                    
                    if (message.hasLocation && message.latitude != null && message.longitude != null) {
                        LocationCard(latitude = message.latitude, longitude = message.longitude)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(message: EmergencyMessage) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sender Name
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
                            text = "Gönderen",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                
                // Sender Number / ID
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
                            text = "Gönderen Numara",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val numText = if (message.senderCountry != null && message.senderPhone != null) {
                            "${message.senderCountry} ${message.senderPhone}"
                        } else {
                            message.senderId
                        }
                        Text(
                            text = numText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

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
                            text = formatTimestampDetail(message.timestampMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                
                // Content
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mesaj",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = message.messageContent,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(latitude: Double, longitude: Double) {
    val location = LatLng(latitude, longitude)
    val cameraPositionState = remember {
        CameraPositionState(position = CameraPosition.fromLatLngZoom(location, 15f))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = location),
                title = "Acil Durum Konumu"
            )
        }
    }
}

private fun formatTimestampDetail(timestampMillis: Long): String {
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
