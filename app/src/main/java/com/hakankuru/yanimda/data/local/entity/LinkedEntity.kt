package com.hakankuru.yanimda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linkeds")
data class LinkedEntity(
    @PrimaryKey var id: String,
    val phone: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    val date: Long,
    val ownerPhone: String,
    var isConfirmed: Boolean
)