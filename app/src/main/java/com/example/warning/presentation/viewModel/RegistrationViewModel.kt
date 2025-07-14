package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.UserRegistrationState
import com.example.warning.domain.usecase.UserRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUse: UserRegistrationUseCase
) : ViewModel(){

    // View'da gözlemlenecek olan state
    val registrationState: StateFlow<UserRegistrationState> = registerUse.state

    fun registerUser(profile: Profile) {
        viewModelScope.launch {
            registerUse.checkAndRegisterUser(profile)
        }
    }
    suspend fun checkingUser(phoneNumber: String): Boolean{
        return registerUse.checkUser(phoneNumber)
    }
}