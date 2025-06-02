package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    var phoneNumber: String, // unique id olarak kullanıyoruz
    var name: String,
    var emergencyMessage: String?
)
