package com.example.warning.domain.model

data class Contact(
    val phoneNumber: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    val ownerPhoneNumber: String,
    val ownerCountry: String,
    var ownerName: String,
    var ownerPhoto: String?,
    var specielMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false
)