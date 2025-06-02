package com.example.warning.data.Dto

import com.example.warning.data.local.entity.ContactEntity

data class UserDto(
    val phoneNumber: String,
    val name: String,
    val contact: List<ContactDto>?,
    val emergencyMessage: String?
)