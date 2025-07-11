package com.example.warning.data.remote.Dto

data class ContactDto(
    val id: String,
    val phone: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    var ownerProfilePhoto: String?,
    val ownerPhone: String,
    val ownerCountry: String,
    var ownerName: String,
    var isActiveUser: Boolean,
    var specialMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false,
    val date: Long
)