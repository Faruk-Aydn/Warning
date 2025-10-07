package com.example.warning.data.remote.Dto

import com.google.firebase.firestore.PropertyName

data class MessagesDto(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val date: Long?,
    val message: String,
    val locationlat: Long?,
    val locationlg: Long?,
    val failureReason: String?,
    @get:PropertyName("isSeen") @set:PropertyName("isSeen")
    var isSeen: Boolean =false,
    @get:PropertyName("isForwarded") @set:PropertyName("isForwarded")
    var isForwarded: Boolean = false
)