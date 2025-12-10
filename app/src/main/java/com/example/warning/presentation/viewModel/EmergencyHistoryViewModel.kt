package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.EmergencyMessage
import com.example.warning.domain.repository.EmergencyHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



data class EmergencyHistoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val allMessages: List<EmergencyMessage> = emptyList(),
    val messages: List<EmergencyMessage> = emptyList(),
    val filter: MessageFilter = MessageFilter.ALL,
    val currentUserId: String? = null,
    val errorMessage: String? = null,
    val selectedMessage: EmergencyMessage? = null,
)

enum class MessageFilter {
    ALL,
    SENT,
    RECEIVED,
}

@HiltViewModel
class EmergencyHistoryViewModel @Inject constructor(
    private val emergencyHistoryRepository: EmergencyHistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyHistoryUiState())
    val uiState: StateFlow<EmergencyHistoryUiState> = _uiState

 
    fun loadHistory(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            emergencyHistoryRepository.getAllMessagesForUser(userId)
                .catch { e ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Bir hata oluştu"
                        )
                    }
                }
                .collect { list ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            currentUserId = userId,
                            allMessages = list,
                            messages = applyFilter(state.filter, list, userId),
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun refresh(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadHistory(userId)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun changeFilter(filter: MessageFilter) {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                filter = filter,
                messages = applyFilter(filter, currentState.allMessages, currentState.currentUserId)
            )
        }
    }

    fun onMessageClick(message: EmergencyMessage) {
        _uiState.update { it.copy(selectedMessage = message) }
    }

    fun onDismissMessageDetail() {
        _uiState.update { it.copy(selectedMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Filtre uygulama mantığı ileride sender/receiver ayrımı netleştiğinde
     * data katmanından gelen modellere göre güncellenebilir.
     */
    private fun applyFilter(
        filter: MessageFilter,
        list: List<EmergencyMessage>,
        currentUserId: String?
    ): List<EmergencyMessage> {
        if (currentUserId.isNullOrEmpty()) return list

        return when (filter) {
            MessageFilter.ALL -> list
            MessageFilter.SENT -> list.filter { it.senderId == currentUserId }
            MessageFilter.RECEIVED -> list.filter { it.receiverId == currentUserId }
        }
    }
}
