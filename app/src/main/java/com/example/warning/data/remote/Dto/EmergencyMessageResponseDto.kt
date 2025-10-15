package com.example.warning.data.remote.Dto

data class EmergencyMessageResponseDto(
    val success: Boolean,
    val message: String,
    val sentCount: Int,
    val failedCount: Int,
    val details: List<MessageDetailDto>? = null
)

data class MessageDetailDto(
    val contactid: String,
    val success: Boolean,
    val messageId: String? = null,
    val usedSpecialMessage: Boolean = false,
    val error: String? = null
)
