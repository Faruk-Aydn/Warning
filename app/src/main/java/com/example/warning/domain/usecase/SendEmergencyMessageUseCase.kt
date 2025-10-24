package com.example.warning.domain.usecase

import android.util.Log
import com.example.warning.data.remote.NetworkModule
import com.example.warning.data.repository.EmergencyResponse
import com.example.warning.data.repository.FcmRepository
import javax.inject.Inject

// UI State Modeli
sealed class EmergencyState {
    object Idle : EmergencyState()
    object Loading : EmergencyState()
    data class Success(val successCount: Int, val failureCount: Int) : EmergencyState()
    data class Error(val message: String) : EmergencyState()
}

class SendEmergencyMessageUseCase @Inject constructor(
    private val repository: FcmRepository
) {
    /**
     * Acil durum mesajı gönderme işlemini yürütür ve UI'ya durum (Loading/Success/Error) akışını sağlar.
     *
     * @return EmergencyState akışı (Flow)
     */
    // NetworkModule'den backendApi al
    val backendApi = NetworkModule.backendApi
    // Repository oluştur
    val fcmRepository = FcmRepository(backendApi)
    suspend fun execute(profileId: String): Result<EmergencyResponse> {
        Log.d("SendProfileIdUseCase", "UseCase başlatıldı. Profile ID: $profileId")
        val result = repository.sendProfileIdToBackend(profileId)

        if (result.isSuccess) {
            Log.d("SendProfileIdUseCase", "Başarılı gönderim: ${result.getOrNull()}")
        } else {
            Log.e("SendProfileIdUseCase", "Hata: ${result.exceptionOrNull()?.message}")
        }

        return result
    }
}
