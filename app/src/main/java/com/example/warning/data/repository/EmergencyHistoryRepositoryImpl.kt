package com.example.warning.data.repository

import com.example.warning.data.local.dao.EmergencyHistoryDao
import com.example.warning.data.local.entity.IncomingEmergencyEntity
import com.example.warning.data.local.entity.OutgoingEmergencyEntity
import com.example.warning.domain.model.EmergencyMessage
import com.example.warning.domain.model.EmergencyMessageStatus
import com.example.warning.domain.repository.EmergencyHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class EmergencyHistoryRepositoryImpl @Inject constructor(
    private val dao: EmergencyHistoryDao
) : EmergencyHistoryRepository {

    override suspend fun getAllMessagesForUser(userId: String): Flow<List<EmergencyMessage>> {
        val incomingFlow = dao.getAllIncoming()
        val outgoingFlow = dao.getAllOutgoing()

        return combine(incomingFlow, outgoingFlow) { incoming, outgoing ->
            val incomingMessages = incoming.map { it.toDomain(userId) }
            val outgoingMessages = outgoing.map { it.toDomain(userId) }

            (incomingMessages + outgoingMessages)
                .sortedByDescending { it.timestampMillis }
        }
    }
}

private fun IncomingEmergencyEntity.toDomain(currentUserId: String): EmergencyMessage {
    val hasLocation = latitude != null && longitude != null
    val locationText = if (hasLocation) {
        "${latitude},${longitude}"
    } else {
        null
    }

    return EmergencyMessage(
        id = id,
        senderId = senderId,
        senderName = senderName ?: "",
        receiverId = currentUserId,
        receiverName = "", // Şimdilik boş, gerekirse Room'a eklenir
        messageContent = messageContent,
        hasLocation = hasLocation,
        locationText = locationText,
        status = EmergencyMessageStatus.DELIVERED,
        isSuccess = true,
        error = null,
        timestampMillis = date,
    )
}

private fun OutgoingEmergencyEntity.toDomain(currentUserId: String): EmergencyMessage {
    val statusEnum = when (status.lowercase()) {
        "sent" -> EmergencyMessageStatus.SENT
        "failed" -> EmergencyMessageStatus.FAILED
        else -> EmergencyMessageStatus.UNKNOWN
    }

    return EmergencyMessage(
        id = id,
        senderId = currentUserId,
        senderName = "", // Şimdilik boş, gerekirse Room'a eklenir
        receiverId = receiverId,
        receiverName = receiverName,
        messageContent = messageContent,
        hasLocation = isLocationSent,
        locationText = null, // Sadece flag var, metin yok
        status = statusEnum,
        isSuccess = success,
        error = error,
        timestampMillis = date,
    )
}
