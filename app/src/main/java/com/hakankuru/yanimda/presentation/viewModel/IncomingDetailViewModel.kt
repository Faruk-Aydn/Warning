package com.hakankuru.yanimda.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.yanimda.domain.model.EmergencyMessage
import com.hakankuru.yanimda.domain.repository.EmergencyHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomingDetailUiState(
    val isLoading: Boolean = true,
    val message: EmergencyMessage? = null,
    val error: String? = null
)

@HiltViewModel
class IncomingDetailViewModel @Inject constructor(
    private val repository: EmergencyHistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val messageId: String = checkNotNull(savedStateHandle["messageId"])
    private val currentUserId: String = checkNotNull(savedStateHandle["currentUserId"])

    private val _uiState = MutableStateFlow(IncomingDetailUiState())
    val uiState: StateFlow<IncomingDetailUiState> = _uiState.asStateFlow()

    init {
        loadMessageDetails()
    }

    private fun loadMessageDetails() {
        viewModelScope.launch {
            _uiState.value = IncomingDetailUiState(isLoading = true)
            try {
                val emergencyMessage = repository.getIncomingMessageById(messageId, currentUserId)
                if (emergencyMessage != null) {
                    _uiState.value = IncomingDetailUiState(
                        isLoading = false,
                        message = emergencyMessage
                    )
                } else {
                    _uiState.value = IncomingDetailUiState(
                        isLoading = false,
                        error = "Mesaj bulunamadı."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = IncomingDetailUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Bilinmeyen bir hata oluştu."
                )
            }
        }
    }
}
