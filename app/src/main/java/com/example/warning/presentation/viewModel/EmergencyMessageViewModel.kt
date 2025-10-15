package com.example.warning.presentation.viewModel

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

    fun sendEmergencyMessage() {
        viewModelScope.launch {
            sendEmergencyMessageUseCase.invoke().collect { result ->
                _emergencyMessageState.value = result
            }
        }
    }

    fun resetState() {
        _emergencyMessageState.value = EmergencyState.Idle
    }
}
