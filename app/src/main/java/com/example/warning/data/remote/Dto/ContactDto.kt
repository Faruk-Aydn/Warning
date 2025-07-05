package com.example.warning.data.remote.Dto

data class ContactDto(
    var phoneNumber: String,
    var name: String?,
    var nickName: String?,
    val ownerPhoneNumber: String?,
    var isActiveUser: Boolean,
    var specielMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false
)
