package com.example.warning.domain.model

data class ProfileUiState(
    val phoneNumber: String = "",
    val name: String = "",
    val emergencyMessage: String? = "",
    val approvedContacts: List<Contact> = emptyList()
)

fun Profile.toUiState(): ProfileUiState {
    return ProfileUiState(
        phoneNumber = phoneNumber,
        name = name,
        emergencyMessage = emergencyMessage,
        approvedContacts = contacts
    )
}