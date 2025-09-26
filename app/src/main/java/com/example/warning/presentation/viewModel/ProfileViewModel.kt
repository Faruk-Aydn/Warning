package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _profileState = MutableStateFlow<Profile?>(null)
    val profileState: StateFlow<Profile?> = _profileState

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _linked = MutableStateFlow<List<Linked>>(emptyList())
    val linked: StateFlow<List<Linked>> = _linked

    fun loadProfile() {
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest {
                _profileState.value = it
            }
        }
        viewModelScope.launch {
            profileUseCases.getAllContact().collectLatest {
                _contacts.value = it
            }
        }
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                _linked.value = it
            }
        }
    }

    fun startListeners(phone: String) {
        viewModelScope.launch {
            profileUseCases.startUserListener(phone)
            profileUseCases.startContactListener(phone)
            profileUseCases.startLinkedListener(phone)
        }
    }

    fun stopListeners() {
        viewModelScope.launch {
            profileUseCases.stopUserListener()
            profileUseCases.stopContactListener()
            profileUseCases.stopLinkedListener()
        }
    }
}