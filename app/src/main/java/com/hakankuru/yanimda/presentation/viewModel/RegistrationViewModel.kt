package com.hakankuru.yanimda.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.yanimda.domain.model.Profile
import com.hakankuru.yanimda.domain.usecase.UserRegistrationUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.checkerframework.checker.units.qual.Prefix.deci
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUse: UserRegistrationUseCase
) : ViewModel(){

    val state: StateFlow<UserRegistrationState> = registerUse.state
    fun registerUser(profile: Profile){
        viewModelScope.launch {
            try {

                // 1. FCM token'ı anlık olarak çek
                val currentToken = FirebaseMessaging.getInstance().getToken().await()

                // 2. Token'ı Profile modeline ekle (Domain modelini kopyalayarak)
                val profileWithToken = profile.copy(fcmToken = currentToken)

                val decide= registerUse.checkAndRegisterUser(profileWithToken)
                Log.i("decide",decide.toString())
            } catch (e: Exception) {
                Log.w("Register",e)
            }
        }
    }

    fun checkingUser(phoneNumber: String) = viewModelScope.async {
        try {
            registerUse.checkUser(phoneNumber)
        } catch (e: Exception) {
            Log.w("checkingUser","check ederken sorun oldu $e")
            false
        }
    }
}