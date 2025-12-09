package com.example.warning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.warning.presentation.ui.screens.AddContactScreen
import com.example.warning.presentation.ui.screens.ContactLinkedScreen
import com.example.warning.presentation.ui.screens.MainScreen
import com.example.warning.presentation.ui.screens.ProfileScreen
import com.example.warning.presentation.ui.screens.Routes
import com.example.warning.presentation.ui.screens.SettingsScreen
import com.example.warning.presentation.ui.screens.SplashScreen
import com.example.warning.presentation.ui.screens.register.SignInScreen
import com.example.warning.presentation.ui.screens.register.SignUpScreen
import com.example.warning.presentation.ui.theme.AppTheme
import com.example.warning.presentation.ui.theme.MyAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme(theme = AppTheme.OCEAN) {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash", // Şu anlık başlangıcı signup yapıyoruz
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = Routes.AddContact) {
                AddContactScreen(navController)
            }
            composable("splash") {
                SplashScreen(
                    navController = navController
                )
            }
            composable("signIn") {
                SignInScreen(
                    navController = navController
                )
            }

            composable("signUp") {
                SignUpScreen(
                    navController = navController
                )
            }
            composable("main") {
                MainScreen(
                    navController = navController
                )
            }
            composable ("profile"){
                ProfileScreen(
                    navController = navController
                )
            }
            composable("settings"){
                SettingsScreen()
            }
            composable("contacts") {
                ContactLinkedScreen(
                    navController= navController
                )
            }
        }
    }
}


/*
📁 app
└── 📁 src
    └── 📁 main
        └── 📁 java
            └── 📁 com
                └── 📁 hakan
                    └── 📁 emergencyapp
                        ├── 📁 presentation
                        │   ├── 📁 profile
                        │   │   ├── 📁 screen
                        │   │   ├── 📁 state
                        │   │   ├── 📁 event
                        │   │   └── 📁 component
                        │   ├── 📁 addcontact
                        │   │   ├── 📁 screen
                        │   │   ├── 📁 event
                        │   │   └── 📁 state
                        │   ├── 📁 requests
                        │   │   ├── 📁 screen
                        │   │   ├── 📁 state
                        │   │   └── 📁 event
                        │   └── 📁 emergency
                        │       ├── 📁 screen
                        │       ├── 📁 state
                        │       └── 📁 event
                        │
                        ├── 📁 domain
                        │   ├── 📁 model
                        │   ├── 📁 usecase
                        │   │   └── 📁 contact
                        │   └── 📁 repository
                        │
                        ├── 📁 data
                        │   ├── 📁 local
                        │   │   ├── 📁 dao
                        │   │   └── 📁 entity
                        │   ├── 📁 remote
                        │   │   ├── 📁 dto
                        │   │   └── 📁 api
                        │   ├── 📁 repository
                        │   └── 📁 mapper
                        │
                        └── 📁 di


*/