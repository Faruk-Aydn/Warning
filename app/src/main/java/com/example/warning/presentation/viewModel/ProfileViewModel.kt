package com.example.warning.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.model.ProfileUiState
import com.example.warning.domain.model.toUiState
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases)
    : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile() {
        viewModelScope.launch {
            profileUseCases.getProfile()?.let { profile ->
                uiState = profile.toUiState()  // İşte burada dönüşüm
            }
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            profileUseCases.deleteContact(contact)
            loadProfile() // Değişiklik sonrası güncelle
        }
    }
    fun deleteAllContact(){
        viewModelScope.launch {
            profileUseCases.deleteAllContact()
        }
    }
    fun deleteProfile(profile: Profile){
        viewModelScope.launch {
            profileUseCases.deleteProfile(profile)
        }
    }
    fun getContactByPhoneNumber(number: String){
        viewModelScope.launch {
            profileUseCases.getContactByPhoneNumber(number)
        }
    }
    fun getAllContact(){
        viewModelScope.launch {
            profileUseCases.getAllContact()
        }
    }
    fun updateProfile(profile: Profile){
        viewModelScope.launch {
            profileUseCases.updateProfile(profile)
        }
    }
    fun getProfile(){
        viewModelScope.launch {
            profileUseCases.getProfile()
        }
    }
}
