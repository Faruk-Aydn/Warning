package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.usecase.LinkedActionResult
import com.example.warning.domain.usecase.LinkedActionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LinkedActionUiState(
    val loadingLinkedId: String? = null,
    val lastResult: LinkedActionResult = LinkedActionResult.Idle
)

@HiltViewModel
class LinkedActionsViewModel @Inject constructor(
    private val linkedActionsUseCase: LinkedActionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinkedActionUiState())
    val uiState: StateFlow<LinkedActionUiState> = _uiState

    fun accept(contactId: String) {
        viewModelScope.launch {
            Log.i("LinkedAction", "accept started for $contactId")
            linkedActionsUseCase.accept(contactId).collect { result ->
                when (result) {
                    is LinkedActionResult.Loading -> _uiState.update { it.copy(loadingLinkedId = contactId, lastResult = result) }
                    is LinkedActionResult.Success -> {
                        Log.i("LinkedAction", "accept success for $contactId")
                        _uiState.update { it.copy(loadingLinkedId = null, lastResult = result) }
                    }
                    is LinkedActionResult.Error -> {
                        Log.w("LinkedAction", "accept error for $contactId: ${result.message}")
                        _uiState.update { it.copy(loadingLinkedId = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun delete(contactId: String) {
        viewModelScope.launch {
            Log.i("LinkedAction", "delete started for $contactId")
            linkedActionsUseCase.delete(contactId).collect { result ->
                when (result) {
                    is LinkedActionResult.Loading -> _uiState.update { it.copy(loadingLinkedId = contactId, lastResult = result) }
                    is LinkedActionResult.Success -> {
                        Log.i("LinkedAction", "delete success for $contactId")
                        _uiState.update { it.copy(loadingLinkedId = null, lastResult = result) }
                    }
                    is LinkedActionResult.Error -> {
                        Log.w("LinkedAction", "delete error for $contactId: ${result.message}")
                        _uiState.update { it.copy(loadingLinkedId = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }
}


