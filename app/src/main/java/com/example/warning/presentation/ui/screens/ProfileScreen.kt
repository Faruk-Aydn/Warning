package com.example.warning.presentation.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    var isZoomed by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val imageSize = if (isZoomed) 250.dp else 150.dp
    val imageAlignment = if (isZoomed) Alignment.Center else Alignment.TopCenter

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = isZoomed) { isZoomed = false } // Zoom modundayken her yere tıklanabilir
            .background(Color(0xFFF2F2F2))
    ) {
        // Geri ve Düzenle Butonları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
            }
            IconButton(onClick = { /* düzenleme sayfasına geç */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Düzenle")
            }
        }

        // Profil Fotoğrafı
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = imageAlignment
        ) {
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { isZoomed = !isZoomed },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { isZoomed = !isZoomed },
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil Fotoğrafı",
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )
                    // SADECE zoom modunda göster
                    if (isZoomed) {
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 8.dp)
                                .size(36.dp)
                                .background(Color.White, shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Fotoğrafı Düzenle")
                        }
                    }
                }
            }
        }

        // Kullanıcı Bilgileri
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ad: Hakan", style = MaterialTheme.typography.bodyLarge)
            Text("Yaş: 24", style = MaterialTheme.typography.bodyLarge)
            Text("Telefon: 0555 555 55 55", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("settings")
            }) {
                Text("Ayarlar")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate("contacts")
            }) {
                Text("Kayıtlı Kişiler")
            }
        }

        // Alt Sayfa (Bottom Sheet)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextButton(onClick = {
                    /* Fotoğrafı kaldır */
                    }) {
                        Text("Fotoğrafı Kaldır")
                    }
                    TextButton(onClick = {
                    /* Galeriden seç */
                    }) {
                        Text("Galeriden Seç")
                    }
                    TextButton(onClick = {
                    /* Kamerayla çek */
                    }) {
                        Text("Kamerayla Çek")
                    }
                }
            }
        }
    }
}
