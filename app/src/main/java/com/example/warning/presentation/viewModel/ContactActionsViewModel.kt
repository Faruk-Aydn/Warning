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
    val loadingContactPhone: String? = null,
    val lastResult: ContactActionResult = ContactActionResult.Idle
)

@HiltViewModel
class ContactActionsViewModel @Inject constructor(
    private val contactActionsUseCase: ContactActionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactActionUiState())
    val uiState: StateFlow<ContactActionUiState> = _uiState

    fun toggleTop(contactPhone: String, currentIsTop: Boolean) {
        viewModelScope.launch {
            Log.i("ContactAction", "toggleTop started for $contactPhone currentIsTop=$currentIsTop")
            contactActionsUseCase.toggleTop(contactPhone, currentIsTop).collect { result ->
                when (result) {
                    is ContactActionResult.Loading -> _uiState.update { it.copy(loadingContactPhone = contactPhone, lastResult = result) }
                    is ContactActionResult.Success -> {
                        Log.i("ContactAction", "toggleTop success for $contactPhone")
                        _uiState.update { it.copy(loadingContactPhone = null, lastResult = result) }
                    }
                    is ContactActionResult.Error -> {
                        Log.w("ContactAction", "toggleTop error for $contactPhone: ${result.message}")
                        _uiState.update { it.copy(loadingContactPhone = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }
    // Removed toggleNotTop: domain flips based on current state


    fun delete(contactPhone: String) {
        viewModelScope.launch {
            Log.i("ContactAction", "delete started for $contactPhone")
            contactActionsUseCase.delete(contactPhone).collect { result ->
                when (result) {
                    is ContactActionResult.Loading -> _uiState.update { it.copy(loadingContactPhone = contactPhone, lastResult = result) }
                    is ContactActionResult.Success -> {
                        Log.i("ContactAction", "delete success for $contactPhone")
                        _uiState.update { it.copy(loadingContactPhone = null, lastResult = result) }
                    }
                    is ContactActionResult.Error -> {
                        Log.w("ContactAction", "delete error for $contactPhone: ${result.message}")
                        _uiState.update { it.copy(loadingContactPhone = null, lastResult = result) }
                    }
                    else -> {}
                }
            }
        }
    }
}


