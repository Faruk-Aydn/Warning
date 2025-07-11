package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "linkeds")
data class LinkedEntity(
    @PrimaryKey val id: String, //Contact id
    val phone: String,
    val country: String,
    var name: String,
    var profilePhoto: String?,
    val date: Long
)