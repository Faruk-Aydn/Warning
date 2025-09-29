package com.example.warning.presentation.viewModel

import android.R.attr.phoneNumber
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileListenerViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _profileState = MutableStateFlow<Profile?>(null)
    val profileState: StateFlow<Profile?> = _profileState

    init {
        // Eğer uygulama açıldığında phoneNumber mevcutsa listener başlat
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest { profile ->
                if (profile != null) {
                    phoneNumber = profile.phoneNumber.toInt()
                    startUserListener(profile.phoneNumber)
                }
            }
        }
    }
    fun loadProfile() {
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest { _profileState.value = it }
        }
    }

    fun startUserListener(phone: String){
        viewModelScope.launch {
            profileUseCases.startUserListener(phone)
        }
    }
    fun stopUserListener(){
         viewModelScope.launch {
             profileUseCases.stopUserListener()
         }
    }
}