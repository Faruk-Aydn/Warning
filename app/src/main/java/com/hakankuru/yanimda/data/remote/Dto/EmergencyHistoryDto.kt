package com.hakankuru.yanimda.data.remote.Dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class EmergencyHistoryDto(
    var id: String = "", // Firestore Document ID
    val senderId: String = "",
    val senderName: String? = null, // index.js içinde log'a eklenmemiş ama eklenebilir, şimdilik opsiyonel
    val senderPhone: String? = null,
    val senderCountry: String? = null,
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverPhone: String? = null,
    val receiverCountry: String? = null,
    val messageContent: String = "",
    val locationSent: Boolean = false,
    val location: FireLocation? = null,
    val status: String = "", // "sent", "failed", "attempted"
    val success: Boolean = false,
    val error: String? = null,

    @ServerTimestamp
    val timestamp: Date? = null // Firestore Timestamp
)

// Firestore'daki iç içe geçmiş 'location' objesini karşılamak için
data class FireLocation(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)