package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val phoneNumber: String, // her kişinin telefon numarası eşsiz
    val name: String
)