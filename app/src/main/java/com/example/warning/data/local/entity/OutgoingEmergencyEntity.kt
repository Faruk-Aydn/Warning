package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outgoing_emergency")
data class OutgoingEmergencyEntity(
    @PrimaryKey
    val id: String, // Firestore ID
    val receiverId: String, // Kime gitti?
    val receiverName: String,
    val messageContent: String,
    val isLocationSent: Boolean,
    val status: String, // "sent", "failed"
    val success: Boolean,
    val error: String?,
    val date: Long
)