package com.example.warning.data.remote.Dto

import java.util.Date
import com.google.firebase.firestore.ServerTimestamp

data class MessageDto(
    val messageId: String?,
    val userId: String,
    val contactId: String,
    val message: String,
    val success: Boolean,
    val error: String?,             // Hata mesajı (başarısızsa)
    @get:ServerTimestamp
    val timestamp: Date? =null // Kayıt zamanı
)