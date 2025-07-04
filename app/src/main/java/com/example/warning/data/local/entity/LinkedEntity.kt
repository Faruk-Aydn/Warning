package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "linked",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["phoneNumber"],
            childColumns = ["ownerPhoneNumber"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class LinkedEntity(
    @PrimaryKey
    val phoneNumber: String,
    var name: String?,
    var nickName: String?,
    val ownerPhoneNumber: String,
    val date: Long
)