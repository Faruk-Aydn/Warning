package com.hakankuru.yanimda.domain.repository

import com.hakankuru.yanimda.domain.model.EmergencyLocation

interface LocationTrackerRepository {
    suspend fun getCurrentLocation(): EmergencyLocation?
}