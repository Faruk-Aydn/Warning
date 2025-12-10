package com.example.warning.domain.model

/**
 * Acil durum mesajı geçmişi için domain model.
 *
 * Firestore tarafındaki doküman alanlarına karşılık gelir ama
 * UI ihtiyacına göre sadeleştirilmiş ve tip güvenli hale getirilmiştir.
 */
data class EmergencyMessage(
    val id: String?,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val messageContent: String,
    val hasLocation: Boolean,
    val locationText: String?,
    val status: EmergencyMessageStatus,
    val isSuccess: Boolean,
    val error: String?,
    val timestampMillis: Long,
)


enum class EmergencyMessageStatus {
    SENT,
    DELIVERED,
    FAILED,
    UNKNOWN,
}
