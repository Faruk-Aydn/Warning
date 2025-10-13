package com.example.warning.data.remote.Dto

import com.google.firebase.firestore.PropertyName

data class UserDto(
    var id: String? = null,
    val phoneNumber: String = "",
    val country: String = "+90",
    var name: String = "",
    var profilePhoto: String? = "",
    var emergencyMessage: String? = null,
    @get:PropertyName("isLocationPermission") @set:PropertyName("isLocationPermission")
    var isLocationPermission: Boolean = false,
    var fcmToken: String?= null
)