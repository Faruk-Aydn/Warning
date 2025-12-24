package com.example.warning.presentation.viewModel

import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val contacts: List<Contact> = emptyList(),
    val linked: List<Linked> = emptyList(),
    val isLocationPermissionGranted: Boolean = false,
    val isContactsPermissionGranted: Boolean = false,
    val errorMessage: String? = null,
)