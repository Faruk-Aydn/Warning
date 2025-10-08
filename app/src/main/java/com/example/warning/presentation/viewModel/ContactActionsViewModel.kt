package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.usecase.ContactActionResult
import com.example.warning.domain.usecase.ContactActionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactActionUiState(
    val loadingContactId: String? = null,
    val lastResult: ContactActionResult = ContactActionResult.Idle
)

@HiltViewModel
class ContactActionsViewModel @Inject constructor(
    private val contactActionsUseCase: ContactActionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactActionUiState())
    val uiState: StateFlow<ContactActionUiState> = _uiState

    fun toggleTop(contactId: String, currentIsTop: Boolean) {
        viewModelScope.launch {
            Log.i("ContactAction", "toggleTop started for $contactId currentIsTop=$currentIsTop")
            contactActionsUseCase.toggleTopById(contactId, currentIsTop).collect { result ->
                when (result) {
                    is ContactActionResult.Loading -> _uiState.update { it.copy(loadingContactId = contactId, lastResult = result) }
                    is ContactActionResult.Success -> {
                        Log.i("ContactAction", "toggleTop success for $contactId")
                        _uiState.update { it.copy(loadingContactId = null, lastResult = result) }
                    }
                    is ContactActionResult.Error -> {
                        Log.w("ContactAction", "toggleTop error for $contactId: ${result.message}")
                        _uiState.update { it.copy(loadingContactId = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }
    // Removed toggleNotTop: domain flips based on current state


    fun delete(contactId: String) {
        viewModelScope.launch {
            Log.i("ContactAction", "delete started for $contactId")
            contactActionsUseCase.deleteById(contactId).collect { result ->
                when (result) {
                    is ContactActionResult.Loading -> _uiState.update { it.copy(loadingContactId = contactId, lastResult = result) }
                    is ContactActionResult.Success -> {
                        Log.i("ContactAction", "delete success for $contactId")
                        _uiState.update { it.copy(loadingContactId = null, lastResult = result) }
                    }
                    is ContactActionResult.Error -> {
                        Log.w("ContactAction", "delete error for $contactId: ${result.message}")
                        _uiState.update { it.copy(loadingContactId = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }
}


