package com.example.warning.domain.model

data class Linked(
    val id: String,
    val phoneNumber: String,
    var name: String,
    val country: String,
    var profilePhoto: String?,
    var ownerPhoneNumber: String,
    val date: Long
)
