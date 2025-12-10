package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incoming_emergency")
data class IncomingEmergencyEntity(
    @PrimaryKey
    val id: String, // Firestore ID
    val senderId: String, // Kim gönderdi?
    val senderName: String?, // Gönderenin adı (Opsiyonel)
    val messageContent: String,
    val latitude: Double?,
    val longitude: Double?,
    val date: Long
)