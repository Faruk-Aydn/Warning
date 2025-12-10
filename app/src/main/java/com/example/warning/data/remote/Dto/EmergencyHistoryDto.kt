package com.example.warning.data.remote.Dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class EmergencyHistoryDto(
    var id: String = "", // Firestore Document ID
    val senderId: String = "",
    val senderName: String? = null, // index.js içinde log'a eklenmemiş ama eklenebilir, şimdilik opsiyonel
    val receiverId: String = "",
    val receiverName: String = "",
    val messageContent: String = "",
    val locationSent: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: String = "", // "sent", "failed", "attempted"
    val success: Boolean = false,
    val error: String? = null,

    @ServerTimestamp
    val timestamp: Date? = null // Firestore Timestamp
)