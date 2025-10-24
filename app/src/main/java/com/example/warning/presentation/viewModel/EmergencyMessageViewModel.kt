package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.usecase.EmergencyState
import com.example.warning.domain.usecase.SendEmergencyMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyMessageViewModel @Inject constructor(
    private val sendEmergencyMessageUseCase: SendEmergencyMessageUseCase
) : ViewModel() {

    private val _emergencyMessageState = MutableStateFlow<EmergencyState>(EmergencyState.Idle)
    val emergencyMessageState: StateFlow<EmergencyState> = _emergencyMessageState.asStateFlow()

    fun sendEmergencyMessage(profileId: String) {
        _emergencyMessageState.value = EmergencyState.Loading
        Log.d("ProfileViewModel", "ID gönderme başlatıldı: $profileId")

        viewModelScope.launch {
            val result = sendEmergencyMessageUseCase.execute(profileId)

            _emergencyMessageState.value = if (result.isSuccess) {
                val response = result.getOrNull()!!
                EmergencyState.Success(
                    successCount = response.successCount,
                    failureCount = response.failureCount
                )
            } else {
                EmergencyState.Error(
                    message = result.exceptionOrNull()?.message ?: "Bilinmeyen hata"
                )
            }

            Log.d("ProfileViewModel", "Güncel state: ${_emergencyMessageState.value}")
        }
    }

    fun resetState() {
        _emergencyMessageState.value = EmergencyState.Idle
    }
}
