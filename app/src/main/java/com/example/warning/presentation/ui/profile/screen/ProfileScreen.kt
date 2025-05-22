package com.example.warning.presentation.ui.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.example.warning.domain.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileEntity,
    onNameChanged: (String) -> Unit,
    onMessageChanged: (String) -> Unit,
    onAddContactClick: () -> Unit,
    onViewRequestsClick: () -> Unit,
    onTestEmergencyClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilim") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kullanıcı Adı
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChanged,
                label = { Text("Adınız") },
                modifier = Modifier.fillMaxWidth()
            )

            // Telefon Numarası (readonly)
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = {},
                label = { Text("Telefon Numaranız") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Acil Durum Mesajı
            OutlinedTextField(
                value = state.emergencyMessage.toString(),
                onValueChange = onMessageChanged,
                label = { Text("Acil Durum Mesajınız") },
                modifier = Modifier.fillMaxWidth()
            )

            // Onaylı Kişi Listesi Başlığı
            Text("Onaylı Kişiler:", style = MaterialTheme.typography.titleMedium)

            // Kişi Listesi
            LazyColumn{
                items(items = state.approvedContacts) { contact ->
                    Text("• ${contact.name} (${contact.phoneNumber})")
                }
            }

            // Butonlar
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onAddContactClick) {
                    Text("Kişi Ekle")
                }

                OutlinedButton(onClick = onViewRequestsClick) {
                    Text("Gelen İstekler")
                }
            }

            // Test Butonu (geçici)
            Button(
                onClick = onTestEmergencyClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Acil Durumu Test Et")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val fakeState = ProfileEntity(
        name = "Hakan Kuru",
        phoneNumber = "+90 532 123 4567",
        emergencyMessage = "Acil durumdayım! Lütfen bana hemen ulaş.",
        approvedContacts = listOf(
            Contact(name = "Ayşe Yılmaz", phoneNumber = "+90 544 987 6543"),
            Contact(name = "Mehmet Can", phoneNumber = "+90 532 111 2233")
        )
    )

    ProfileScreen(
        state = fakeState,
        onNameChanged = {},
        onMessageChanged = {},
        onAddContactClick = {},
        onViewRequestsClick = {},
        onTestEmergencyClick = {}
    )
}