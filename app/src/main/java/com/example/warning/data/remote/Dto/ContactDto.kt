package com.example.warning.data.remote.Dto

import androidx.annotation.Keep

@Keep
data class ContactDto(
    var id: String = "",
    var phone: String = "",
    var country: String = "",
    var name: String = "",
    var profilePhoto: String? = null,
    var ownerProfilePhoto: String? = null,
    var ownerPhone: String = "",
    var ownerCountry: String = "",
    var ownerName: String = "",
    var isActiveUser: Boolean = false,
    var specialMessage: String? = null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean = false,
    var isConfirmed: Boolean = false,
    var date: Long = 0L
)