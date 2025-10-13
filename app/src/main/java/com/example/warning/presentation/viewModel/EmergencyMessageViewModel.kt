package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.usecase.EmergencyMessageResult
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

    private val _emergencyMessageState = MutableStateFlow<EmergencyMessageResult>(EmergencyMessageResult.Idle)
    val emergencyMessageState: StateFlow<EmergencyMessageResult> = _emergencyMessageState.asStateFlow()

    fun sendEmergencyMessage() {
        viewModelScope.launch {
            sendEmergencyMessageUseCase.execute().collect { result ->
                _emergencyMessageState.value = result
            }
        }
    }

    fun resetState() {
        _emergencyMessageState.value = EmergencyMessageResult.Idle
    }
}
