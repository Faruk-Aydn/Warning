package com.example.warning.presentation.ui.screens
/*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userExists by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            userExists = profileViewModel.getProfile() != null
        }
    }

    if (userExists == null) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (userExists == true) {
        LaunchedEffect(Unit) {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
    } else {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
*/