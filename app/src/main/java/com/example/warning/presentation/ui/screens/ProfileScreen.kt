package com.example.warning.presentation.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.warning.domain.usecase.ProfileUseCases
import com.example.warning.presentation.viewModel.ProfileViewModel
import com.google.android.play.integrity.internal.f
import com.google.firebase.firestore.auth.User
import kotlin.math.log


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val profile by viewModel.profileState.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val linked by viewModel.linked.collectAsState()

    val contactPermission= false
    when (profile == null) {

        true -> Log.d("ProfileScreen", "Profil verisi boş geldi: ")
        false -> return
    }

    LaunchedEffect(Unit) {
        viewModel.startListeners(profile!!.phoneNumber)
        viewModel.loadProfile()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopListeners() }
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