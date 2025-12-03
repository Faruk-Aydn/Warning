package com.example.warning.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.example.warning.domain.model.UserLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AndroidLocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation? {
        // Tek seferlik, en son bilinen veya anlık konum denemesi
        val location: Location? = try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
        } catch (e: Exception) {
            null
        }

        val safeLocation = location ?: return null

        return UserLocation(
            latitude = safeLocation.latitude,
            longitude = safeLocation.longitude,
            accuracy = safeLocation.accuracy,
            time = safeLocation.time
        )
    }
}
