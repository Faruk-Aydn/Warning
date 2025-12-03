package com.example.warning.domain.usecase


import com.example.warning.domain.model.EmergencyLocation
import com.example.warning.domain.model.EmergencySendResult
import com.example.warning.domain.repository.EmergencyRepository
import javax.inject.Inject

class SendEmergencyMessageUseCase @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) {
    /**
     * UseCase: ViewModel sadece bunu çağıracak.
     * Lokasyon şimdilik burada sahte oluşturuluyor.
     */
    suspend operator fun invoke(): EmergencySendResult {
        // ŞİMDİLİK SAHTE LOKASYON
        // Sonra buraya gerçek LocationProvider'dan veri gelecek.
        val fakeLocation = EmergencyLocation(
            latitude = 41.015137,   // İstanbul örnek
            longitude = 28.979530
        )

        return emergencyRepository.sendEmergency(fakeLocation)
    }
}