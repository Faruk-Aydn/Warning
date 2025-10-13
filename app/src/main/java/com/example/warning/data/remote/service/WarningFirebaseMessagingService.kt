package com.example.warning.data.remote.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.warning.domain.usecase.UpdateFCMTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WarningFirebaseMessagingService : FirebaseMessagingService() {

    // Hilt ile Use Case'i enjekte et
    @Inject lateinit var updateFCMTokenUseCase: UpdateFCMTokenUseCase

    private val TAG = "WarningFCMService"

    /**
     * Yeni bir FCM token'ı oluşturulduğunda veya mevcut token güncellendiğinde çağrılır.
     * Bu, uygulamanın yeniden yüklenmesi, veri silinmesi, yeni cihaz kullanımı gibi durumlarda olur.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Yeni token'ı kaydetmek için Use Case'i çalıştır
        sendRegistrationToServer(token)
    }

    /**
     * Gelen FCM mesajlarını (bildirimler ve veri mesajları) işler.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Mesajın veri payload'u var mı kontrol et
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Buraya acil durum bildirimi işleme mantığı gelir
            // Örneğin: Acil durum mesajıysa, yerel bir bildirim göster, haritayı güncelle vb.
        }

        // Mesajın bildirim payload'u var mı kontrol et (Notification mesajları için)
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Genellikle acil durumlar için özel veri mesajları kullanılır,
            // ancak bildirim de buradan işlenebilir.
        }
    }

    private fun sendRegistrationToServer(token: String) {
        // Ağ işlemleri ve veritabanı yazma işlemleri için IO CoroutineScope kullanılır
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = updateFCMTokenUseCase.execute(token)
                if (success) {
                    Log.i(TAG, "FCM token başarıyla Remote/Local veritabanlarına kaydedildi.")
                } else {
                    Log.e(TAG, "FCM token kaydı başarısız.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Token'ı güncellerken beklenmedik hata", e)
            }
        }
    }
}