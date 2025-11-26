package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.usecase.EmergencyState
import com.example.warning.domain.usecase.SendEmergencyMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyMessageViewModel @Inject constructor(
    private val sendEmergencyMessageUseCase: SendEmergencyMessageUseCase
) : ViewModel() {

    private val _emergencyMessageState = MutableStateFlow<EmergencyState>(EmergencyState.Idle)
    val emergencyMessageState: StateFlow<EmergencyState> = _emergencyMessageState

    fun sendEmergencyMessage() {
        // UI'dan gelen tetikleme: Butona basınca burası çağrılıyor.
        viewModelScope.launch {
            try {
                _emergencyMessageState.value = EmergencyState.Loading

                val result = sendEmergencyMessageUseCase()

                _emergencyMessageState.value = EmergencyState.Success(
                    successCount = result.successCount,
                    failureCount = result.failureCount
                )
            } catch (e: Exception) {
                // Şimdilik basit hata yakalama
                _emergencyMessageState.value =
                    EmergencyState.Error(e.message ?: "Bilinmeyen bir hata oluştu")
            }
        }
    }

    fun resetState() {
        _emergencyMessageState.value = EmergencyState.Idle
    }
}