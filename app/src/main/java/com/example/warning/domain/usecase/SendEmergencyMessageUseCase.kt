package com.example.warning.domain.usecase

import android.util.Log
import com.example.warning.data.mapper.toDomain
import com.example.warning.domain.repository.EmergencyMessageRepository
import com.example.warning.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

// UI State Modeli
sealed class EmergencyState {
    object Idle : EmergencyState()
    object Loading : EmergencyState()
    data class Success(val successCount: Int, val failureCount: Int) : EmergencyState()
    data class Error(val message: String) : EmergencyState()
}

class SendEmergencyMessageUseCase @Inject constructor(
    private val repository: EmergencyMessageRepository
) {
    /**
     * Acil durum mesajı gönderme işlemini yürütür ve UI'ya durum (Loading/Success/Error) akışını sağlar.
     *
     * @return EmergencyState akışı (Flow)
     */
    operator fun invoke(): Flow<EmergencyState> = flow {
        // 1. Loading durumunu emit et
        emit(EmergencyState.Loading)

        try {
            // 2. Repository çağrısı
            val (successCount, failureCount) = repository.sendEmergencyMessageToContacts()

            // 3. Başarı durumunu emit et
            emit(EmergencyState.Success(successCount, failureCount))

        } catch (e: IllegalStateException) {
            // Profil bulunamadı gibi iş mantığı hataları
            Log.e("UseCase", "İş Mantığı Hatası: ${e.message}", e)
            emit(EmergencyState.Error("İşlem başlatılamadı: ${e.message}"))
        } catch (e: IOException) {
            // Ağ veya I/O ile ilgili hatalar
            Log.e("UseCase", "Ağ/I/O Hatası: ${e.message}", e)
            emit(EmergencyState.Error("Ağ bağlantı hatası. Lütfen kontrol edin."))
        } catch (e: Exception) {
            // Diğer tüm beklenmedik hatalar
            Log.e("UseCase", "Beklenmedik Hata: ${e.message}", e)
            emit(EmergencyState.Error("Acil durum mesajı gönderme sırasında beklenmedik bir hata oluştu."))
        }
    }
}
