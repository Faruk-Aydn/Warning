package com.example.warning.data.repository

import android.util.Log
import com.example.warning.data.remote.service.EmergencyMessageService
import com.example.warning.data.remote.Dto.EmergencyMessageRequestDto
import com.example.warning.data.mapper.toDomain
import com.example.warning.data.remote.Dto.ContactDetailDto
import com.example.warning.data.remote.Dto.MessageDto
import com.example.warning.data.remote.service.FirestoreLogService
import com.example.warning.domain.model.EmergencyMessageResponse
import com.example.warning.domain.repository.EmergencyMessageRepository
import com.example.warning.domain.repository.ProfileRepository
import java.util.UUID
import javax.inject.Inject

class EmergencyMessageRepositoryImpl @Inject constructor(
    private val messageService: EmergencyMessageService,
    private val profileRepository: ProfileRepository,
    private val logService: FirestoreLogService
) : EmergencyMessageRepository {

    private val TAG = "EmergencyRepository"

    /**
     * Acil durum mesajını filtrelediği contact'lara gönderir, logları kaydeder ve sonucu döndürür.
     *
     * @return Pair<Int, Int> (Başarılı Gönderim Sayısı, Başarısız Gönderim Sayısı)
     */
    override suspend fun sendEmergencyMessageToContacts(): Pair<Int, Int> {
        // 1. Gönderici ve Contact listesini al
        val currentUser = profileRepository.getCurrentUserOnce()
            ?: throw IllegalStateException("Gönderici profili bulunamadı.")

        val contacts = profileRepository.getContactOnce()

        // 2. Filtreleme: isConfirmed && isTop == true olan contact'lar
        val filteredContacts = contacts.filter { it.isConfirmed && it.isTop }

        if (filteredContacts.isEmpty()) {
            Log.w(TAG, "Gönderilecek onaylanmış/üst sırada contact bulunamadı.")
            return Pair(0, 0)
        }

        val messageId = UUID.randomUUID().toString()
        val logList = mutableListOf<MessageDto>()
        var successCount = 0
        var failureCount = 0

        // 3. Her contact için mesaj seçimi ve Service çağrısı
        filteredContacts.forEach { contact ->
            // Mesaj Seçimi Mantığı:
            val messageToSend = contact.specielMessage
                ?: currentUser.emergencyMessage
                ?: "yardım" // Varsayılan mesaj

            val log = try {
                // FCM Servis Çağrısı
                val result = messageService.sendEmergencyMessage(
                    // TODO: bura dikkat
                    senderId = currentUser.id.toString(),
                    receiverId = contact.id, // Veya fcmToken'ı kullanabilirsiniz, servis tanımınıza bağlı
                    message = messageToSend
                )

                if (result.isSuccess) {
                    successCount++
                    MessageDto(
                        messageId = messageId,
                        userId = currentUser.id.toString(),
                        contactId = contact.id,
                        message = messageToSend,
                        success = true,
                        error = null,
                        timestamp = TODO(),
                    )
                } else {
                    failureCount++
                    val error = result.exceptionOrNull()?.message ?: "Bilinmeyen FCM hatası"
                    MessageDto(
                        messageId = messageId,
                        userId = currentUser.id.toString(),
                        contactId = contact.id,
                        message = "mesaj gönderilemedi",
                        success = false,
                        error = error
                    )
                }
            } catch (e: Exception) {
                // Service çağrısı sırasında oluşan beklenmedik (network/kod) hatalar
                Log.e(TAG, "FCM gönderme hatası: ContactID=${contact.id}, Hata: ${e.message}", e)
                failureCount++
                MessageDto(
                    messageId = messageId,
                    userId = currentUser.id.toString(),
                    contactId = contact.id,
                    message = "Hata",
                    success = false,
                    error = "uygulama hatası ${e.message}"
                )
            }
            logList.add(log)
        }

        // 4. Logları Firestore'a kaydetmek için logService'i çağır
        try {
            logService.saveMessageLogs(logList)
        } catch (e: Exception) {
            // Log kaydetme başarısız olsa bile, mesaj gönderme süreci UI'ya rapor edilmelidir.
            Log.e(TAG, "Tüm logları kaydetme hatası: ${e.message}", e)
            // Hatanın UseCase'e taşınması gerekip gerekmediği mimarinize bağlı, burada devam ediyoruz.
        }

        // 5. Başarılı / başarısız sayısını döndür
        return Pair(successCount, failureCount)
    }
}
