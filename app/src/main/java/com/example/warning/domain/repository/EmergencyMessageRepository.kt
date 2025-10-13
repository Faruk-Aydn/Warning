package com.example.warning.domain.repository

import com.example.warning.domain.model.EmergencyMessageResponse

interface EmergencyMessageRepository {
    suspend fun sendEmergencyMessage(): Result<EmergencyMessageResponse>
}
