package com.example.warning.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.warning.domain.usecase.AddContactUseCase
import com.example.warning.domain.usecase.AddContactResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListenerViewmodel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val addContactUseCase: AddContactUseCase
) : ViewModel(){

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _linked = MutableStateFlow<List<Linked>>(emptyList())
    val linked: StateFlow<List<Linked>> = _linked

    private var contactsCollectStarted: Boolean = false
    private var linkedCollectStarted: Boolean = false

    init {
        // Eğer uygulama açıldığında phoneNumber mevcutsa listener başlat
        viewModelScope.launch {
            profileUseCases.getProfile().collectLatest { profile ->
                if (profile != null) {
                    val phoneNumber = profile.phoneNumber
                    startContactListener(phoneNumber)
                    startCollectContactsIfNeeded()
                    startCollectLinkedIfNeeded()
                }
            }
        }
    }

    private fun startCollectContactsIfNeeded() {
        if (contactsCollectStarted) return
        contactsCollectStarted = true
        viewModelScope.launch {
            profileUseCases.getAllContact().collectLatest {
                _contacts.value = it ?: emptyList()
            }
        }
    }

    private fun startCollectLinkedIfNeeded() {
        if (linkedCollectStarted) return
        linkedCollectStarted = true
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                _linked.value = it ?: emptyList()
            }
        }
    }

    fun loadContact() {
        startCollectContactsIfNeeded()
        startCollectLinkedIfNeeded()
        Log.i("loadContact", "listener yükledi")
        Log.i("loadLinked", "listener yükledi")
    }
    fun loadLinked() {
        startCollectLinkedIfNeeded()
        Log.i("loadLinked", "listener yükledi")
    }

    fun startContactListener(phone: String){
        viewModelScope.launch {
            profileUseCases.startContactListener(phone)
            profileUseCases.startLinkedListener(phone)
        }
    }

    private val _addContactState = MutableStateFlow<AddContactResult>(AddContactResult.Idle)
    val addContactState: StateFlow<AddContactResult> = _addContactState

    fun addContact(phone: String, country: String){
        viewModelScope.launch {
            try {
                addContactUseCase.execute(phone, country).collect {
                    _addContactState.value = it
                }
            } catch (e: Exception) {
                _addContactState.value = AddContactResult.Error(e.message ?: "Bilinmeyen hata")
            }
        }
    }
    fun stopContactListener(){
        viewModelScope.launch {
            profileUseCases.stopContactListener()
            profileUseCases.stopLinkedListener()
        }
    }

}