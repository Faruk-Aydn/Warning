package com.example.warning.data.remote.Dto

data class UserDto(
    var id: String? = null,
    val phoneNumber: String = "",
    val country: String = "+90",
    var name: String = "",
    var profilePhoto: String? = "",
    var emergencyMessage: String? = null,
    var isLocationPermission: Boolean = false
)