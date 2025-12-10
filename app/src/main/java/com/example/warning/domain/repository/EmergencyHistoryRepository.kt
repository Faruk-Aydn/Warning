package com.example.warning.domain.repository

import com.example.warning.domain.model.EmergencyMessage

/**
 * Acil durum mesaj geçmişini okumak için domain seviye repository.
 *
 * UI sadece bu interface üzerinden geçmiş mesajlara erişmelidir.
 * Veri kaynağı (Firestore, Room vb.) data katmanında soyutlanır.
 */
interface EmergencyHistoryRepository {

    /**
     * Belirli bir kullanıcının ilişkili olduğu tüm acil durum mesajlarını döner.
     *
     * Uygulama kuralına göre userId; senderId, receiverId veya her ikisine göre
     * filtrelenebilir. Bu ayrıntı data katmanında çözülecektir.
     */
    suspend fun getAllMessagesForUser(userId: String): List<EmergencyMessage>
}
