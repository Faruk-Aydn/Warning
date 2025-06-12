package com.example.warning.presentation.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.warning.presentation.ui.theme.DrawerBackground
import com.example.warning.presentation.ui.theme.DrawerItemText
import com.example.warning.presentation.viewModel.ProfileViewModel

@Composable
fun WarningButton(
    buttonSize: Int,
    viewModel: ProfileViewModel = hiltViewModel()
){
    viewModel.loadProfile()
    Canvas(modifier = Modifier.size((buttonSize+15).dp)) {
        drawCircle(
            color = Color.Red.copy(alpha = 0.3f),
            style = Stroke(width = 4.dp.toPx())
        )
    }
    Canvas(modifier = Modifier.size((buttonSize+30).dp)) {
        drawCircle(
            color = Color.Red.copy(alpha = 0.3f),
            style = Stroke(width = 2.dp.toPx())
        )
    }

    Button(
        onClick = { /* TODO: Acil durum işlemi */ },
        modifier = Modifier
            .size(buttonSize.dp)
            .clip(shape = CircleShape),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text("ACİL", color = Color.White)
    }
}

@Composable
fun MenuContent(
    onClose: ()-> Unit,
    navController: NavController
){
    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan tıklaması ile menüyü kapat
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onClose() }
        )
        // Menünün kendisi (sağ tarafta dikey ve dar)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(180.dp)
                .background(
                    color = DrawerBackground.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                )
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            // Menü simgesi sağ üstte
            Spacer(modifier = Modifier.height(20.dp))
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Icon",
                tint = DrawerItemText,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Ayarlar", modifier = Modifier.clickable {
                    onClose()
                    navController.navigate("settings")
                }.padding(vertical = 8.dp), color = DrawerItemText)
                Text("Yardım", modifier = Modifier.clickable {
                    onClose()
                    navController.navigate("help")
                }.padding(vertical = 8.dp), color = DrawerItemText)
                Spacer(modifier = Modifier.weight(1f))
                Text("v1.0.0", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isProfileDialogOpen by remember { mutableStateOf(false) }
    val buttonSize: Int = 150

    Box(
        modifier = Modifier
            .fillMaxSize().background(
                Brush.verticalGradient(colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B)))
            )
    )
    {
        // Üst bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profil butonu
            IconButton(onClick = { navController.navigate("profile") }) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Varsayılan Profil İkonu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Menü butonu
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            WarningButton(buttonSize = buttonSize)
        }
        // Menü içeriği
        if (isMenuExpanded) {
            MenuContent(
                onClose = { isMenuExpanded = false },
                navController= navController
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPre(){

}
