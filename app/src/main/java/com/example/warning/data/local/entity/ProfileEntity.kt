package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val phone: String, // unique id olarak kullanıyoruz
    val country: String,
    var name: String,
    var profilePhoto: String,
    var emergencyMessage: String? = null,
    var locationPermission: Boolean = false,
    var contactPermission: Boolean = false,
    var linked: String?,
    var contact: String?,
    var messageReceiver: String?,
    var messageSented: String?
)
