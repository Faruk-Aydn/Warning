package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["phoneNumber"],
            childColumns = ["ownerPhoneNumber"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerPhoneNumber")]
)
data class ContactEntity(
    @PrimaryKey val phoneNumber: String,  // İletişim kişisinin numarası
    val name: String,
    val ownerPhoneNumber: String // Hangi profile ait olduğunu belirtir
)
