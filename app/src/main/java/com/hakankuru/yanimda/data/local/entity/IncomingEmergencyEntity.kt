package com.hakankuru.yanimda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incoming_emergency")
data class IncomingEmergencyEntity(
    @PrimaryKey
    val id: String, // Firestore ID
    val senderId: String, // Kim gönderdi?
    val senderName: String?, // Gönderenin adı (Opsiyonel)
    val senderPhone: String?,
    val senderCountry: String?,
    val receiverPhone: String?,
    val receiverCountry: String?,
    val isLocationSent: Boolean,
    val messageContent: String,
    val latitude: Double?,
    val longitude: Double?,
    val date: Long
)