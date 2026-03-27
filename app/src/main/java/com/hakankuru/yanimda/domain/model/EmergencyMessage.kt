package com.hakankuru.yanimda.domain.model

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
    val senderPhone: String?,
    val senderCountry: String?,
    val receiverId: String,
    val receiverName: String,
    val receiverPhone: String?,
    val receiverCountry: String?,
    val messageContent: String,
    val hasLocation: Boolean,
    val locationText: String?,
    val status: EmergencyMessageStatus,
    val isSuccess: Boolean,
    val error: String?,
    val timestampMillis: Long,
    val latitude: Double? = null,
    val longitude: Double? = null
)


enum class EmergencyMessageStatus {
    SENT,
    DELIVERED,
    FAILED,
    UNKNOWN,
}
