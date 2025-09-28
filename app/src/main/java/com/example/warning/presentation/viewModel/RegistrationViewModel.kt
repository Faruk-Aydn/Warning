package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.UserRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.Prefix.deci
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUse: UserRegistrationUseCase
) : ViewModel(){

    fun registerUser(profile: Profile){
        viewModelScope.launch {
            try {
                val decide= registerUse.checkAndRegisterUser(profile)
                Log.i("decide",decide.toString())
            } catch (e: Exception) {
                Log.w("Register",e)
            }
        }
    }

    fun checkingUser(phoneNumber: String) = viewModelScope.async {
        try {
            registerUse.checkUser(phoneNumber)
            true
        } catch (e: Exception) {
            Log.w("checkingUser","check ederken sorun oldu $e")
            false
        }
    }
}