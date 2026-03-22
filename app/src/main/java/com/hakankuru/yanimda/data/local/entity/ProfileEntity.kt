package com.hakankuru.yanimda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val phone: String, // unique id olarak kullanıyoruz
    val country: String,
    var name: String,
    var profilePhoto: String,
    var emergencyMessage: String? = null,
    var locationPermission: Boolean = false,
    var fcmToken: String? = null // FCM Token
)
