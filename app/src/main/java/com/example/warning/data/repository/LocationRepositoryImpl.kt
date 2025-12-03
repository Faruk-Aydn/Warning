package com.example.warning.data.repository

import com.example.warning.data.location.AndroidLocationProvider
import com.example.warning.domain.model.UserLocation
import com.example.warning.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationProvider: AndroidLocationProvider
) : LocationRepository {

    override suspend fun getCurrentLocation(): UserLocation? {
        return locationProvider.getCurrentLocation()
    }
}
