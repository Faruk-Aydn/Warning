package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    suspend fun isRegistered(){
        return
    }
    // Kayıt işlemi (basit doğrulama ile)
    fun register(profile: Profile, onResult: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                profileUseCases.updateProfile(profile) // updateProfile aslında kayıt/güncelleme işlevi görebilir
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}