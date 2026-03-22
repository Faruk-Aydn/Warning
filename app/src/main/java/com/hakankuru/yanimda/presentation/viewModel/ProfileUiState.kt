package com.hakankuru.yanimda.presentation.viewModel

import com.hakankuru.yanimda.domain.model.Contact
import com.hakankuru.yanimda.domain.model.Linked
import com.hakankuru.yanimda.domain.model.Profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val contacts: List<Contact> = emptyList(),
    val linked: List<Linked> = emptyList(),
    val isLocationPermissionGranted: Boolean = false,
    val isContactsPermissionGranted: Boolean = false,
    val errorMessage: String? = null,
)