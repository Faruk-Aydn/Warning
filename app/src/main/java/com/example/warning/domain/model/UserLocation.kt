package com.example.warning.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val time: Long? = null
)
