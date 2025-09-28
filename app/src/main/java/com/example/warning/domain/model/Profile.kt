package com.example.warning.domain.model

data class Profile(
    val id: String?,
    val phoneNumber: String,
    val country: String,
    var profilePhoto: String?,
    val name: String,
    var emergencyMessage: String?,
    var locationPermission: Boolean = false
)