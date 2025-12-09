package com.example.warning.data.repository

import com.example.warning.data.remote.Dto.EmergencyRequestDto
import com.example.warning.data.remote.api.EmergencyApi
import com.example.warning.domain.model.EmergencyLocation
import com.example.warning.domain.model.EmergencySendResult
import com.example.warning.domain.repository.EmergencyRepository
import javax.inject.Inject


class EmergencyRepositoryImpl @Inject constructor(
    private val api: EmergencyApi
) : EmergencyRepository {

    override suspend fun sendEmergency (location: EmergencyLocation, senderId: String): EmergencySendResult {
        // Domain model -> DTO dönüşümü
        val request = EmergencyRequestDto(
            latitude = location.latitude,
            longitude = location.longitude,
            senderId = senderId
        )

        // Backend'e istek
        val response = api.sendEmergency(request)

        // DTO -> Domain model
        return EmergencySendResult(
            successCount = response.successCount,
            failureCount = response.failureCount
        )
    }

}