package com.example.warning.data.repository

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.warning.domain.model.EmergencyLocation
import com.example.warning.domain.repository.LocationTrackerRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import javax.inject.Inject
import android.content.Context
import kotlinx.coroutines.tasks.await
import com.google.android.gms.location.Priority


class LocationTrackerRepositoryImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTrackerRepository {

    override suspend fun getCurrentLocation(): EmergencyLocation? {
        // 1. İzin Kontrolü
        val hasAccessFineLocation = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocation = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // 2. GPS/Network Açık mı Kontrolü
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!hasAccessCoarseLocation && !hasAccessFineLocation || !isGpsEnabled) {
            return null
        }

        return try {
            // ADIM 3: Önce "Taze" (Canlı) Konumu almaya çalış
            var location = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()

            // ADIM 4: Eğer canlı konum alınamadıysa (null ise), "Son Bilinen Konum"a başvur
            if (location == null) {
                // lastLocation: Cihazın hafızasındaki son geçerli konumdur.
                location = locationClient.lastLocation.await()
            }

            // Sonuç varsa Domain modeline çevir, yoksa null dön
            location?.let {
                EmergencyLocation(
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        } catch (e: Exception) {
            // Herhangi bir hata durumunda (Timeout vb.) son şans olarak yine lastLocation dene
            try {
                val lastKnown = locationClient.lastLocation.await()
                lastKnown?.let {
                    EmergencyLocation(it.latitude, it.longitude)
                }
            } catch (e2: Exception) {
                null
            }
        }
    }
}