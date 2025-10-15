package com.example.warning.data.remote.service

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log // Android Log kullanımı
import com.example.warning.data.remote.Dto.MessageDto
import kotlin.collections.first
import kotlin.collections.firstOrNull

class FirestoreLogService(private val firestore: FirebaseFirestore) {

    private val TAG = "FirestoreLogService"

    /**
     * Mesaj loglarını Firestore'a toplu olarak kaydeder.
     * Koleksiyon Yapısı: messages/{messageId}/logs/{contactId}
     *
     * @param logs Kaydedilecek MessageLog DTO'ları listesi.
     */
    suspend fun saveMessageLogs(logs: List<MessageDto>) {
        if (logs.isEmpty()) return

        // Toplu yazma (Batch write) ile tüm logları tek bir işlemde kaydetmek daha verimli
        val batch = firestore.batch()

        logs.forEach { log ->
            try {
                // messages/{messageId} belgesini al
                val messageDocRef = firestore.collection("messages").document(log.messageId)

                // logs alt koleksiyonuna {contactId} adıyla logu ekle
                val logDocRef = messageDocRef.collection("logs").document(log.contactId)

                // Batch'e yazma işlemini ekle
                batch.set(logDocRef, log)
            } catch (e: Exception) {
                // Log oluşturma döngüsü içinde bir hata olursa (örn: log alanında sorun)
                Log.e(TAG, "Log kaydı hazırlama hatası: ContactID=${log.contactId}, Hata: ${e.message}", e)
                // Bu durumda batch'e eklenmeyen loglar kaybolur, ancak diğerleri denenecektir.
            }
        }

        try {
            // Batch işlemini tamamla
            batch.commit().await()
            Log.d(TAG, "${logs.size} adet log Firestore'a başarıyla kaydedildi. MessageID: ${logs.first().messageId}")
        } catch (e: Exception) {
            // Firestore commit işlemi sırasında oluşan genel hata
            Log.e(TAG, "Firestore log kaydetme hatası: MessageID=${logs.firstOrNull()?.messageId}, Hata: ${e.message}", e)
            throw RuntimeException("Log kaydetme işlemi başarısız oldu.", e)
        }
    }
}