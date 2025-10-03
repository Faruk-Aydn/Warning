package com.example.warning.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBar
import androidx.navigation.NavController
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactLinkedScreen(
    viewModel: ContactListenerViewmodel = hiltViewModel(),
    navController: NavController
) {
    val contacts by viewModel.contacts.collectAsState()
    val linked by viewModel.linked.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0=Contacts, 1=Linked
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadContact()
    }

    // Yenileme (Pull-to-refresh simülasyonu)
    fun refresh() {
        scope.launch {
            isRefreshing = true
            viewModel.loadContact()
            kotlinx.coroutines.delay(1000) // loading simülasyonu
            isRefreshing = false
        }
    }
   
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts & Linked") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("settings"){
                            launchSingleTop = true
                            popUpTo(Routes.Settings) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Segmented Control
            SmoothSegmentedToggle(
                options = listOf("Contacts", "Linked"),
                selectedIndex = selectedTab,
                onOptionSelected = { selectedTab = it }
            )

            Spacer(Modifier.height(8.dp))

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { refresh() }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedTab == 0) {
                        // CONTACTS LIST
                        items(contacts) { contact ->
                            ContactRow(contact)
                        }
                    } else {
                        // LINKED LIST
                        items(linked) { link ->
                            LinkedRow(link)
                        }
                    }
                }
            }
            // İçerik
            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ContactRow(contact: Contact) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("${contact.name} (${contact.tag ?: ""})") },
        supportingContent = { Text("${contact.country} ${contact.phoneNumber}") },
        trailingContent = {
            Row {
                if (contact.isTop) {
                    IconButton(onClick = { /* toggle favori */ }) {
                        Icon(Icons.Filled.Star, "Favori")
                    }
                } else {
                    IconButton(onClick = { /* toggle favori */ }) {
                        Icon(Icons.Outlined.FavoriteBorder, "Not Favori")
                    }
                }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Text("${contact.name} - ${contact.phoneNumber}", Modifier.padding(8.dp))
                    DropdownMenuItem(
                        text = { Text("İptal Et") },
                        onClick = { showDialog = true; expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Düzenle") },
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Evet") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Hayır") }
            },
            title = { Text("Emin misiniz?") }
        )
    }
}

@Composable
fun LinkedRow(linked: Linked) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text("${linked.name}") },
        supportingContent = { Text("${linked.country} ${linked.phoneNumber}") },
        trailingContent = {
            Row {
                if (!linked.isConfirmed) {
                    TextButton(onClick = { showDialog = true }) { Text("Kabul Et") }
                }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Text("${linked.name} - ${linked.phoneNumber}", Modifier.padding(8.dp))
                    DropdownMenuItem(
                        text = { Text("İptal Et") },
                        onClick = { showDialog = true; expanded = false }
                    )
                }
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Kabul Et") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Reddet") }
            },
            title = { Text("İşlemi onayla") }
        )
    }
}
