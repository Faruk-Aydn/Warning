package com.example.warning.data.remote.Dto

data class UserDto(
    val phoneNumber: String,
    val name: String,
    val contact: List<ContactDto?>,
    val emergencyMessage: String?
)