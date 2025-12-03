package com.example.warning.domain.usecase

import com.example.warning.domain.model.UserLocation
import com.example.warning.domain.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): UserLocation? {
        return locationRepository.getCurrentLocation()
    }
}
