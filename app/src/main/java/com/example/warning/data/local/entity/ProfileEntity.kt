package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val phoneNumber: String, // unique id olarak kullanıyoruz
    var name: String,
    var emergencyMessage: String? = null,
    var locationPermission: Boolean = false,
    var ContactPermission: Boolean = false
)
