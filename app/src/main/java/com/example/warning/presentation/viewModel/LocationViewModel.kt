package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.UserLocation
import com.example.warning.domain.usecase.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LocationUiState {
    object Idle : LocationUiState()
    object Loading : LocationUiState()
    data class Success(val location: UserLocation) : LocationUiState()
    data class Error(val message: String) : LocationUiState()
}

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _locationState = MutableStateFlow<LocationUiState>(LocationUiState.Idle)
    val locationState: StateFlow<LocationUiState> = _locationState.asStateFlow()

    fun fetchLocation() {
        _locationState.value = LocationUiState.Loading
        viewModelScope.launch {
            try {
                val location = getCurrentLocationUseCase()
                if (location != null) {
                    _locationState.value = LocationUiState.Success(location)
                } else {
                    _locationState.value = LocationUiState.Error("Konum alınamadı")
                }
            } catch (e: Exception) {
                _locationState.value = LocationUiState.Error(e.message ?: "Bilinmeyen hata")
            }
        }
    }
}
