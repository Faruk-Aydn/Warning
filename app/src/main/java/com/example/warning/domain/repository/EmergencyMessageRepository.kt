package com.example.warning.domain.repository

interface EmergencyMessageRepository {
    suspend fun sendEmergencyMessageToContacts(): Pair<Int, Int>
}