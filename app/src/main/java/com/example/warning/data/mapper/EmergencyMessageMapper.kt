package com.example.warning.data.mapper

import com.example.warning.data.remote.Dto.EmergencyMessageResponseDto
import com.example.warning.data.remote.Dto.MessageDetailDto
import com.example.warning.domain.model.EmergencyMessageResponse
import com.example.warning.domain.model.MessageDetail

fun EmergencyMessageResponseDto.toDomain(): EmergencyMessageResponse {
    return EmergencyMessageResponse(
        success = success,
        message = message,
        sentCount = sentCount,
        failedCount = failedCount,
        details = details?.map { it.toDomain() }
    )
}

fun MessageDetailDto.toDomain(): MessageDetail {
    return MessageDetail(
        contactPhone = contactPhone,
        contactName = contactName,
        success = success,
        messageId = messageId,
        usedSpecialMessage = usedSpecialMessage,
        error = error
    )
}
