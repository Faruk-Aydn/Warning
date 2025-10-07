package com.example.warning.data.remote.Dto

import com.google.firebase.firestore.PropertyName

data class LinkedDto(
    val id: String, //Contact id
    val phone: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    val ownerPhone: String,
    val date: Long,
    @get:PropertyName("isConfirmed") @set:PropertyName("isConfirmed")
    var isConfirmed: Boolean
)