package com.example.warning.domain.model

data class EmergencyMessageResponse(
    val success: Boolean,
    val message: String,
    val sentCount: Int,
    val failedCount: Int,
    val details: List<MessageDetail>? = null
)

data class MessageDetail(
    val contactPhone: String,
    val contactName: String,
    val success: Boolean,
    val messageId: String? = null,
    val usedSpecialMessage: Boolean = false,
    val error: String? = null
)
