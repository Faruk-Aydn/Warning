package com.example.warning.domain.model

data class Contact(
    val id: String,
    val phoneNumber: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    val ownerPhoneNumber: String,
    var specielMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false,
    var isConfirmed: Boolean,
    var fcmToken: String? = null, // FCM Token
)