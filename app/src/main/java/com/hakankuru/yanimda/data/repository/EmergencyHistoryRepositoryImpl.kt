package com.hakankuru.yanimda.data.repository

import com.hakankuru.yanimda.data.local.dao.EmergencyHistoryDao
import com.hakankuru.yanimda.data.local.entity.IncomingEmergencyEntity
import com.hakankuru.yanimda.data.local.entity.OutgoingEmergencyEntity
import com.hakankuru.yanimda.domain.model.EmergencyMessage
import com.hakankuru.yanimda.domain.model.EmergencyMessageStatus
import com.hakankuru.yanimda.domain.repository.EmergencyHistoryRepository
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

    override suspend fun getIncomingMessageById(id: String, currentUserId: String): EmergencyMessage? {
        return dao.getIncomingById(id)?.toDomain(currentUserId)
    }

    override suspend fun getOutgoingMessageById(id: String, currentUserId: String): EmergencyMessage? {
        return dao.getOutgoingById(id)?.toDomain(currentUserId)
    }

    override suspend fun clearEmergencyHistory() {
        dao.clearIncoming()
        dao.clearOutgoing()
    }
}

private fun IncomingEmergencyEntity.toDomain(currentUserId: String): EmergencyMessage {
    val locationText = if (isLocationSent && latitude != null && longitude != null) {
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
        hasLocation = isLocationSent,
        locationText = locationText,
        status = EmergencyMessageStatus.DELIVERED,
        isSuccess = true,
        error = null,
        timestampMillis = date,
        latitude = latitude,
        longitude = longitude
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
        latitude = latitude,
        longitude = longitude
    )
}
