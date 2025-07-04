package com.example.warning.data.remote.Dto

data class UserDto(
    val phoneNumber: String,
    var name: String,
    var contact: List<ContactDto?>,
    var emergencyMessage: String?,
    var isLocationPermission: Boolean =false,
    var ContactPermission: Boolean = false,
    var linked: List<LinkedDto?>
)