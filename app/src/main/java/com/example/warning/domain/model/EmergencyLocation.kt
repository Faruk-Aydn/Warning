package com.example.warning.domain.model

data class EmergencyLocation(
    val latitude: Double,
    val longitude: Double
)

data class EmergencySendResult(
    val successCount: Int,
    val failureCount: Int
)