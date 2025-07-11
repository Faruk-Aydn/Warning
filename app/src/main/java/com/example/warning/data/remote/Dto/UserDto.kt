package com.example.warning.data.remote.Dto

data class UserDto(
    val phoneNumber: String,
    val country: String,
    var name: String,
    var profilePhoto: String,
    var emergencyMessage: String?,
    var isLocationPermission: Boolean =false,
    var isContactPermission: Boolean = false,
    var contact: List<Int?>,
    var linked: List<Int?>,
    var messageReceiver: List<Int?>,
    var messageSented: List<Int?>
)