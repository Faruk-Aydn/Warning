package com.example.warning.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.warning.presentation.ui.screens.ContactLinkedScreen
import com.example.warning.presentation.ui.screens.MainScreen
import com.example.warning.presentation.ui.screens.SplashScreen
import com.example.warning.presentation.ui.screens.register.SignInScreen
import com.example.warning.presentation.viewModel.ContactActionsViewModel
import com.example.warning.presentation.viewModel.ContactListenerViewmodel
import com.example.warning.presentation.viewModel.EmergencyMessageViewModel
import com.example.warning.presentation.viewModel.ProfileListenerViewModel
import com.example.warning.presentation.viewModel.VerificationViewModel

@Composable
fun WarningNavGraph(
    modifier: Modifier = Modifier
){}/*
TODO:
{
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        // --- SPLASH SCREEN ---
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // --- SIGN IN SCREEN ---
        composable(Routes.SIGN_IN) {
            val viewModel: VerificationViewModel = hiltViewModel()
            // Ekranın içindeki logic'i buraya callback olarak çekiyoruz
            SignInScreen(
                navController = navController,
                onVerificationSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        // --- MAIN SCREEN ---
        composable(Routes.MAIN) {
            val profileVm: ProfileListenerViewModel = hiltViewModel()
            val contactVm: ContactListenerViewmodel = hiltViewModel()
            val emergencyVm: EmergencyMessageViewModel = hiltViewModel()

            val profileState by profileVm.profileState.collectAsState()
            val emergencyState by emergencyVm.emergencyMessageState.collectAsState()

            MainScreen(
                navController = navController,
                profile = profileState,
                emergencyState = emergencyState,
                onEmergencyClick = { emergencyVm.sendEmergencyMessage() },
                onResetEmergencyState = { emergencyVm.resetState() }
            )
        }

        // --- CONTACTS SCREEN ---
        composable(Routes.CONTACTS) {
            val viewModel: ContactListenerViewmodel = hiltViewModel()
            val actionsVm: ContactActionsViewModel = hiltViewModel()

            ContactLinkedScreen(
                navController = navController,
                onAddContactClick = { navController.navigate(Routes.AddContact) }
            )
        }
    }
}
*/
