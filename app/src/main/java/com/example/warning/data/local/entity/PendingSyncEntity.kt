package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    val syncType: SyncType,  // Enum: PROFILE_UPDATE, CONTACT_UPDATE vs.
    val timestamp: Long = System.currentTimeMillis()
)

enum class SyncType {
    PROFILE_UPDATE
}