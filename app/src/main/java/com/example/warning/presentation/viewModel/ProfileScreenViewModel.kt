package com.example.warning.presentation.viewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.example.warning.domain.usecase.ProfileUseCases
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isContactsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    private val profileFlow = flow { emitAll(profileUseCases.getProfile()) }
        .distinctUntilChanged()
        .onEach { profile ->
            if (profile != null) {
                startListeners(profile.phoneNumber)
            }
        }

    private val contactsFlow = flow { emitAll(profileUseCases.getAllContact()) }
        .distinctUntilChanged()

    private val linkedFlow = flow { emitAll(profileUseCases.getAllLinked()) }
        .distinctUntilChanged()

    val uiState: StateFlow<ProfileUiState> =
        combine(profileFlow, contactsFlow, linkedFlow) { profile, contacts, linked ->
            ProfileUiState(
                isLoading = profile == null,
                profile = profile,
                contacts = contacts.orEmpty(),
                linked = linked.orEmpty(),
                isLocationPermissionGranted = isLocationPermissionGranted(),
                isContactsPermissionGranted = isContactsPermissionGranted(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(isLoading = true),
        )

    private fun startListeners(phone: String) {
        viewModelScope.launch {
            profileUseCases.startUserListener(phone)
            profileUseCases.startContactListener(phone)
            profileUseCases.startLinkedListener(phone)
        }
    }
}
