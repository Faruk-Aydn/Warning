package com.example.warning.domain.repository

import com.example.warning.domain.model.UserLocation

interface LocationRepository {
    suspend fun getCurrentLocation(): UserLocation?
}
