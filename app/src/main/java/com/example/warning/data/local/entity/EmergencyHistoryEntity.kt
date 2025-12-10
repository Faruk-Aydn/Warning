package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_history")
data class EmergencyHistoryEntity(
    @PrimaryKey
    val id: String, // Firestore ID
    val senderId: String,
    val receiverId: String,
    val receiverName: String,
    val messageContent: String,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val date: Long, // Room için Timestamp -> Long dönüşümü

    // Bu kaydın o anki kullanıcı için ne ifade ettiğini belirten alanlar
    val isIncoming: Boolean // True ise bana gelmiştir (Receiver benim), False ise ben göndermişimdir (Sender benim)
)