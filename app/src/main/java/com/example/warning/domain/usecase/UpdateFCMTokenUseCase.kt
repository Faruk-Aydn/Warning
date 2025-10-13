package com.example.warning.domain.usecase

import android.util.Log
import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateFCMTokenUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val profileRepository: ProfileRepository
) {
    private val TAG = "UpdateFCMTokenUseCase"

    suspend fun execute(newToken: String): Boolean {
        Log.i(TAG, "FCM token güncelleme başlatıldı.")

        // 1. Mevcut kullanıcı profilini Room'dan al
        val currentUser = profileRepository.getCurrentUserOnce() // [cite: 161, 194]

        if (currentUser?.id == null) {
            Log.e(TAG, "Mevcut kullanıcı bulunamadı veya Firestore ID'si eksik.")
            return false
        }

        // Eğer token zaten aynıysa güncellemeye gerek yok (optimistik kontrol)
        if (currentUser.fcmToken == newToken) {
            Log.d(TAG, "FCM token zaten güncel. Güncelleme atlandı.")
            return true
        }

        val userId = currentUser.id!!

        // 2. Token'ı Firebase'de güncelle
        val remoteOk = firebaseRepository.updateFCMToken(userId, newToken)

        if (!remoteOk) {
            Log.e(TAG, "Firebase token güncelleme başarısız oldu.")
            return false
        }

        // 3. Token'ı yerel Room veritabanında güncelle (UserRealtimeSyncManager zaten bunu yapabilir,
        // ancak senkronizasyon için beklememek adına manuel olarak da güncelleyebiliriz.)
        // Veya daha iyisi: Profili manuel olarak güncellemek yerine,
        // UserRealtimeSyncManager'ın Firestore'dan gelen güncel veriyi (token dahil)
        // almasını ve ProfileEntity'ye yazmasını sağlamak.

        // Güvenilir olması için: RealtimeSyncManager'ın token'ı güncellediğini varsayıyoruz.
        // Kayıt ve giriş işlemleri zaten real-time listener'ları başlatıyor[cite: 108, 115, 158].

        Log.i(TAG, "FCM token başarıyla güncellendi (Firebase).")
        return true
    }
}