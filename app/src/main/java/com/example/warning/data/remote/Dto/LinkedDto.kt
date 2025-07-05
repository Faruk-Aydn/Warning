package com.example.warning.data.remote.Dto

data class LinkedDto(
    val phoneNumber: String,
    var name: String?,
    var nickName: String?,
    val ownerPhoneNumber: String,
    val date: Long
)