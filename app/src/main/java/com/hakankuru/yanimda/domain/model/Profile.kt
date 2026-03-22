package com.hakankuru.yanimda.domain.model

data class Profile(
    val id: String?,
    var fcmToken: String? = null, // Kullanıcının cihazına ait FCM Token
    val phoneNumber: String,
    val country: String,
    var profilePhoto: String?,
    val name: String,
    var emergencyMessage: String?,
    var locationPermission: Boolean = false,
)