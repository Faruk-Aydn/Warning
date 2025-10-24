package com.example.warning.data.repository

import android.util.Log
import com.example.warning.data.remote.Dto.MessageDto
import com.example.warning.domain.repository.EmergencyMessageRepository
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.domain.usecase.EmergencyState
import java.util.UUID
import javax.inject.Inject

class EmergencyMessageRepositoryImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
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
        Log.i("profile", "${currentUser.fcmToken} and id: ${currentUser.id}")

        val contacts = profileRepository.getContactOnce()

        // 2. Filtreleme: isConfirmed && isTop == true olan contact'lar
        val filteredContacts = contacts.filter { it.isConfirmed && it.isTop }

        if (filteredContacts.isEmpty()) {
            Log.w(TAG, "Gönderilecek onaylanmış/üst sırada contact bulunamadı.")
            return Pair(0, 0)
        }
        val timeInMilliSeconds: Long = 1678886400000L

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
                Log.e("çağrı öncesi 1","currentUser: ${currentUser.id} contact addedId: ${contact.addedId} contact id: ${contact.id} ")
                if (currentUser.id ==null || contact.addedId == null)
                    Log.e("repositoryImpl 2","currentUser: ${currentUser.id} contact addedId: ${contact.addedId} contact id: ${contact.id}")
                // FCM Servis Çağrısı
                val result = messageService.sendEmergencyMessage(
                    // TODO: bura dikkat
                    senderId = currentUser.id!!,
                    receiverId = contact.addedId!!, // Veya fcmToken'ı kullanabilirsiniz, servis tanımınıza bağlı
                    message = messageToSend,
                    title ="title"
                )

                if (result.isSuccess) {
                    successCount++
                    Log.w("successCount", "succes ${successCount}")

                    MessageDto(
                        messageId = null,
                        userId = currentUser.id,
                        contactId = contact.id,
                        message = messageToSend,
                        success = true,
                        error = null,
                        timestamp = java.util.Date(timeInMilliSeconds),
                    )
                } else {
                    failureCount++
                    val error = result.exceptionOrNull()?.message ?: "Bilinmeyen FCM hatası"
                    Log.w("failureCount", "${error}")
                    MessageDto(
                        messageId = null,
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
                    messageId = null,
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
            Log.d(TAG, "Log listesi boyutu: ${filteredContacts.size}. İlk log contactName: ${filteredContacts.firstOrNull()?.name}")

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
