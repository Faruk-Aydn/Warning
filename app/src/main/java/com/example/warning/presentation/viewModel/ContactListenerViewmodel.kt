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
        Log.i("loadContact", "listener yükledi")
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                if (it != null) {
                    _linked.value = it
                }
            }
        }
        Log.i("loadLinked", "listener yükledi")
    }
    fun loadLinked() {
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                if (it != null) {
                    _linked.value = it
                }
            }
        }
        Log.i("loadContact", "listener yükledi")
        viewModelScope.launch {
            profileUseCases.getAllLinked().collectLatest {
                if (it != null) {
                    _linked.value = it
                }
            }
        }
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