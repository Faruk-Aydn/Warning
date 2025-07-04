package com.example.warning.domain.model

data class Profile(
    val phoneNumber: String,
    val name: String,
    val emergencyMessage: String?,
    val contacts: List<Contact>,
    var locationPermission: Boolean = false,
    var ContactPermission: Boolean = false
)