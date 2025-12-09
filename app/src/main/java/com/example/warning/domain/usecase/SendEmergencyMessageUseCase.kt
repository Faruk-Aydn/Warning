package com.example.warning.domain.usecase


import android.util.Log
import com.example.warning.domain.model.EmergencyLocation
import com.example.warning.domain.model.EmergencySendResult
import com.example.warning.domain.repository.EmergencyRepository
import com.example.warning.domain.repository.LocationTrackerRepository
import com.example.warning.domain.repository.ProfileRepository
import javax.inject.Inject

class SendEmergencyMessageUseCase @Inject constructor(
    private val emergencyRepository: EmergencyRepository,
    private val locationTracker: LocationTrackerRepository, // Buraya enjekte ettik
    private val profileRepo: ProfileRepository
) {
    /**
     * UseCase: ViewModel sadece bunu çağıracak.
     * Lokasyon şimdilik burada sahte oluşturuluyor.
     */
    suspend operator fun invoke(): EmergencySendResult {

        // 1. Kullanıcı Kimliğini (ID) Al
        val currentUser = profileRepo.getCurrentUserOnce()

        /// Eğer kullanıcı yoksa veya ID null ise işlem yapamayız
        if (currentUser?.id == null) {
            Log.e("EmergencyUseCase", "Kullanıcı ID'si bulunamadı, acil durum gönderilemiyor.")
            return EmergencySendResult(0, 0) // Veya hata durumu döndürebilirsin
        }

        // 2. Gerçek Konumu Al (LocationTrackerImpl'de yazdığımız mantık çalışır)
        // Eğer konum null gelirse (0.0, 0.0) gönderiyoruz (Null Island).
        val realLocation = locationTracker.getCurrentLocation() ?: EmergencyLocation(0.0, 0.0)

        // 3. ID ve Konumu Birleştirip Gönder
        return emergencyRepository.sendEmergency(realLocation, currentUser.id)
    }
}