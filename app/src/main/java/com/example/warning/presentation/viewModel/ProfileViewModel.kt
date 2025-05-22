package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.ProfileUiState
import com.example.warning.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.warning.domain.model.toUiState


@HiltViewModel
class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            val profile = repository.getProfile()
            profile?.let {
                _profileState.value = it.toUiState()
            }
        }
    }
}
