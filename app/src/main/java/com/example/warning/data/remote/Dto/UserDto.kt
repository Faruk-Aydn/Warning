package com.example.warning.data.remote.Dto

data class UserDto(
    val phoneNumber: String = "",
    val country: String = "",
    var name: String = "",
    var profilePhoto: String = "",
    var emergencyMessage: String? = null,
    var isLocationPermission: Boolean = false
)
