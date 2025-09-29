package com.example.warning.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListenerViewmodel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel(){

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _linked = MutableStateFlow<List<Linked>>(emptyList())
    val linked: StateFlow<List<Linked>> = _linked

    init {
        // Eğer uygulama açıldığında phoneNumber mevcutsa listener başlat
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest { profile ->
                if (profile != null) {
                    val phoneNumber = profile.phoneNumber
                    startContactListener(phoneNumber)
                }
            }
        }
    }
    fun loadContact() {
        viewModelScope.launch {
            profileUseCases.getAllContact().collectLatest {
                if (it != null) {
                    _contacts.value = it
                }
            }
        }
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                if (it != null) {
                    _linked.value = it
                }
            }
        }
    }

    fun startContactListener(phone: String){
        viewModelScope.launch {
            profileUseCases.startContactListener(phone)
            profileUseCases.startLinkedListener(phone)
        }
    }
    fun stopContactListener(){
        viewModelScope.launch {
            profileUseCases.stopContactListener()
            profileUseCases.stopLinkedListener()
        }
    }

}