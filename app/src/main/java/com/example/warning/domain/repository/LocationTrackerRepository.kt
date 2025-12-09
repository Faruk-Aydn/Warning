package com.example.warning.domain.repository

import com.example.warning.domain.model.EmergencyLocation

interface LocationTrackerRepository {
    suspend fun getCurrentLocation(): EmergencyLocation?
}