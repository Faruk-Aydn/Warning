package com.hakankuru.yanimda.domain.repository

import com.hakankuru.yanimda.domain.model.EmergencyLocation
import com.hakankuru.yanimda.domain.model.EmergencySendResult


interface EmergencyRepository {
    /**
     * Acil durum mesajı gönder. Backend tarafına konuşan tek nokta burası olacak.
     *
     * @param location   - Şimdilik sahte, sonra gerçek GPS verisi gelecek
     */
    suspend fun sendEmergency(location: EmergencyLocation, senderId: String): EmergencySendResult
}