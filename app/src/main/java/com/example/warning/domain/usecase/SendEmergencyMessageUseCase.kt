package com.example.warning.domain.usecase

import com.example.warning.domain.repository.EmergencyMessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

sealed class EmergencyMessageResult {
    object Idle : EmergencyMessageResult()
    object Loading : EmergencyMessageResult()
    data class Success(
        val sentCount: Int,
        val failedCount: Int,
        val message: String
    ) : EmergencyMessageResult()
    data class Error(val message: String) : EmergencyMessageResult()
}

class SendEmergencyMessageUseCase @Inject constructor(
    private val emergencyMessageRepository: EmergencyMessageRepository
) {
    suspend fun execute(): Flow<EmergencyMessageResult> = flow {
        emit(EmergencyMessageResult.Loading)

        try {
            val result = emergencyMessageRepository.sendEmergencyMessage()
            
            if (result.isSuccess) {
                val successResult = result.getOrNull()
                emit(EmergencyMessageResult.Success(
                    sentCount = successResult?.sentCount ?: 0,
                    failedCount = successResult?.failedCount ?: 0,
                    message = successResult?.message ?: "Mesajlar gönderildi"
                ))
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Bilinmeyen hata"
                emit(EmergencyMessageResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(EmergencyMessageResult.Error(e.message ?: "Bilinmeyen hata"))
        }
    }
}
