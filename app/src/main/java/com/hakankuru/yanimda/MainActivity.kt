package com.hakankuru.yanimda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hakankuru.yanimda.presentation.ui.navigation.WarningNavGraph
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hakankuru.yanimda.presentation.ui.theme.WarningTheme
import com.hakankuru.yanimda.presentation.viewModel.MainViewModel
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
            val mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState(initial = false)

            WarningTheme(darkTheme = isDarkTheme) {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    WarningNavGraph(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
            }
        }
    }
}
