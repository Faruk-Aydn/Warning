package com.example.warning.presentation.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel


@Composable
fun ProfileScreen(
    viewModel: ProfileListenerViewModel = hiltViewModel(),
    contactViewModel: ContactListenerViewmodel = hiltViewModel(),
    navController: NavController
) {
    val profile by viewModel.profileState.collectAsState()
    val contacts by contactViewModel.contacts.collectAsState()
    val linked by contactViewModel.linked.collectAsState()

    val contactPermission= false
    when (profile == null) {

        true -> Log.d("ProfileScreen", "Profil verisi boş geldi: ")
        false -> return
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Box(){
        if (profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profil bilgileri
                item {

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = profile!!.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = profile!!.phoneNumber)
                    Text(text = "Ülke: ${profile!!.country}")
                    profile!!.emergencyMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Acil Mesaj: $it")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Konum İzni: ${if (profile!!.locationPermission) "Verildi" else "Verilmedi"}")
                    Text("Kişi İzni: ${if (contactPermission) "Verildi" else "Verilmedi"}")
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Contact listesi
                item { Text("📇 Kişiler", style = MaterialTheme.typography.titleMedium) }
                items(contacts.size) { index ->
                    Text("- ${contacts[index].name} (${contacts[index].phoneNumber})")
                }
                item{
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Linked listesi
                item { Text("🔗 Linked Hesaplar", style = MaterialTheme.typography.titleMedium) }
                items(linked.size) { index ->
                    Text("- ${linked[index].phoneNumber}")
                }
            }
        }
    }
}