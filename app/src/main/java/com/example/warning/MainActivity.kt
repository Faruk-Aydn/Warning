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
import com.example.warning.presentation.ui.navigation.WarningNavGraph
import com.example.warning.presentation.ui.screens.AddContactScreen
import com.example.warning.presentation.ui.screens.ContactLinkedScreen
import com.example.warning.presentation.ui.screens.EmergencyHistoryScreen
import com.example.warning.presentation.ui.screens.MainScreen
import com.example.warning.presentation.ui.screens.ProfileScreen
import com.example.warning.presentation.ui.screens.Routes
import com.example.warning.presentation.ui.screens.SettingsScreen
import com.example.warning.presentation.ui.screens.SplashScreen
import com.example.warning.presentation.ui.screens.register.SignInScreen
import com.example.warning.presentation.ui.screens.register.SignUpScreen
import com.example.warning.presentation.ui.theme.WarningTheme
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: com.example.warning.presentation.viewModel.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState(initial = false)

            WarningTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                WarningNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
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