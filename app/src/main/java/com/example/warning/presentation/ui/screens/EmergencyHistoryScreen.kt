package com.example.warning.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val uiState = viewModel.uiState.value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        val profile = authViewModel.isLoggedIn()
        val userId = profile?.id ?: profile?.phoneNumber
        if (userId != null) {
            viewModel.loadHistory(userId)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Acil Mesaj Geçmişi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            FilterBar(
                selectedFilter = uiState.filter,
                onFilterSelected = { filter -> viewModel.changeFilter(filter) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Yükleniyor...")
                }
            } else if (uiState.messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Henüz acil durum mesajı bulunmuyor")
                }
            } else {
                EmergencyHistoryList(
                    uiState = uiState,
                    onItemClick = { message -> viewModel.onMessageClick(message) }
                )
            }
        }
    }
}

@Composable
private fun EmergencyHistoryList(
    uiState: EmergencyHistoryUiState,
    onItemClick: (EmergencyMessage) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.messages) { message ->
            EmergencyHistoryItem(
                message = message,
                onClick = { onItemClick(message) }
            )
            Divider()
        }
    }
}

@Composable
private fun EmergencyHistoryItem(
    message: EmergencyMessage,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = " → ",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = message.receiverName,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = message.messageContent,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val statusText = when {
                !message.isSuccess -> "HATA"
                else -> message.status.name
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = formatTimestamp(message.timestampMillis),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun FilterBar(
    selectedFilter: MessageFilter,
    onFilterSelected: (MessageFilter) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            text = "Tümü",
            isSelected = selectedFilter == MessageFilter.ALL,
            onClick = { onFilterSelected(MessageFilter.ALL) }
        )
        FilterChip(
            text = "Gönderilen",
            isSelected = selectedFilter == MessageFilter.SENT,
            onClick = { onFilterSelected(MessageFilter.SENT) }
        )
        FilterChip(
            text = "Alınan",
            isSelected = selectedFilter == MessageFilter.RECEIVED,
            onClick = { onFilterSelected(MessageFilter.RECEIVED) }
        )
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Button(
        onClick = onClick,
        colors = colors,
        modifier = Modifier
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
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
