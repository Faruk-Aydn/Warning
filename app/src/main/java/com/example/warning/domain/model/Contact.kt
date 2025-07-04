package com.example.warning.domain.model

data class Contact(
    val phoneNumber: String,
    val name: String?,
    var nickname: String?,
    val ownerPhoneNumber: String,
    var isActiveUser: Boolean,
    var specielMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false

)