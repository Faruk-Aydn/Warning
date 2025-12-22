package com.example.warning.presentation.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.TopAppBar
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactLinkedScreen(
    contacts: List<Contact>,
    linked: List<Linked>,
    isRefreshing: Boolean,
    isContactLoading: (Contact) -> Boolean,
    isLinkedLoading: (Linked) -> Boolean,
    onBack: () -> Unit,
    onAddContactClick: () -> Unit,
    onRefresh: () -> Unit,
    onToggleTop: (Contact) -> Unit,
    onDeleteContact: (Contact) -> Unit,
    onAcceptLinked: (Linked) -> Unit,
    onDeleteLinked: (Linked) -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0=Contacts, 1=Linked
   
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bağlantılar", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onAddContactClick()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contact")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
                    // Segmented Control
                    SmoothSegmentedToggle(
                        options = listOf("Contacts", "Linked"),
                        selectedIndex = selectedTab,
                        onOptionSelected = { selectedTab = it }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { onRefresh() }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedTab == 0) {
                        // CONTACTS LIST
                        items(items = contacts, key = { it.id }) { contact ->
                            ContactRow(
                                contact,
                                isContactLoading(contact),
                                onToggleTop = {
                                    onToggleTop(contact)
                                },
                                onDeleteConfirmed = {
                                    onDeleteContact(contact)
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    } else {
                        // LINKED LIST
                        items(items = linked, key = { it.id }) { link ->
                            LinkedRow(
                                linked = link,
                                isLoading = isLinkedLoading(link),
                                onAccept = { onAcceptLinked(link) },
                                onDelete = { onDeleteLinked(link) }
                            )
                            Spacer(Modifier.height(8.dp))
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
fun ContactRow(
    contact: Contact,
    isLoading: Boolean,
    onToggleTop: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(
                text = buildString {
                    append(contact.name)
                    contact.tag?.takeIf { it.isNotBlank() }?.let {
                        append(" (")
                        append(it)
                        append(")")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = "${contact.country} ${contact.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingContent = {
            Row {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    IconButton(onClick = { onToggleTop() }) {
                        if (contact.isTop) {
                            Icon(Icons.Filled.Star, "Favori")
                        } else {
                            Icon(Icons.Outlined.FavoriteBorder, "Not Favori")
                        }
                    }
                }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Text(
                        text = "${contact.name} - ${contact.phoneNumber}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Bağlantıyı kaldır",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        onClick = { showDialog = true; expanded = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Düzenle",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        onClick = { /* ContactDetailScreen oluşturup genel düzenleme yapabilecek */ }
                    )
                }
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onDeleteConfirmed() }) {
                    Text(
                        text = "Evet",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = "Hayır",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            title = { Text("Emin misiniz?", style = MaterialTheme.typography.titleMedium) }
        )
    }
}

@Composable
fun LinkedRow(
    linked: Linked,
    isLoading: Boolean,
    onAccept: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(
                text = linked.name,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = "${linked.country} ${linked.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        trailingContent = {
            Row {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (!linked.isConfirmed) {
                        TextButton(onClick = { onAccept() }) {
                            Text(
                                text = "Kabul Et",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    Text("${linked.name} - ${linked.phoneNumber}", Modifier.padding(8.dp))
                    DropdownMenuItem(
                        text = { Text("Bağlantıyı kaldır") },
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
                TextButton(onClick = { showDialog = false; onDelete() }) {
                    Text(
                        text = "Evet",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = "Hayır",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            title = { Text("İşlemi onayla", style = MaterialTheme.typography.titleMedium) }
        )
    }
}
