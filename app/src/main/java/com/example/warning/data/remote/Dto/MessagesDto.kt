package com.example.warning.data.remote.Dto

data class MessagesDto(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val date: Long?,
    val message: String,
    val locationlat: Long?,
    val locationlg: Long?,
    val failureReason: String?,
    var isSeen: Boolean =false,
    var isForwarded: Boolean = false
)