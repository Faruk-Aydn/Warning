package com.example.warning.data.remote.Dto

/**
 * Backend'e gidecek acil durum isteği DTO'su.
 * Şu an minimal tutuldu. İleride senderId, message vs. eklersin.
 */
data class EmergencyRequestDto(
    val latitude: Double,
    val longitude: Double
)