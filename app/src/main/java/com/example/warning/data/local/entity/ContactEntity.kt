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
    var name: String? =null,
    var nickName: String?= null,
    val ownerPhoneNumber: String, // Hangi profile ait olduğunu belirtir
    var isActiveUser: Boolean,
    var specielMessage: String?= null,
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    var isTop: Boolean= false
)
