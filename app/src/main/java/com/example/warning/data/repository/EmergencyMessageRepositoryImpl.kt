package com.example.warning.data.repository

import com.example.warning.data.remote.service.EmergencyMessageService
import com.example.warning.data.remote.Dto.EmergencyMessageRequestDto
import com.example.warning.data.mapper.toDomain
import com.example.warning.domain.model.EmergencyMessageResponse
import com.example.warning.domain.repository.EmergencyMessageRepository
import com.example.warning.domain.repository.ProfileRepository
import javax.inject.Inject

class EmergencyMessageRepositoryImpl @Inject constructor(
    private val emergencyMessageService: EmergencyMessageService,
    private val profileRepository: ProfileRepository
) : EmergencyMessageRepository {

    override suspend fun sendEmergencyMessage(): Result<EmergencyMessageResponse> {
        return try {
            // Mevcut kullanıcının telefon numarasını al
            val currentUser = profileRepository.getCurrentUserOnce()
            if (currentUser == null) {
                return Result.failure(Exception("Kullanıcı bilgisi bulunamadı"))
            }

            val phoneNumber = currentUser.phoneNumber
            if (phoneNumber.isNullOrEmpty()) {
                return Result.failure(Exception("Telefon numarası bulunamadı"))
            }

            // Cloud Function'a istek gönder
            val request = EmergencyMessageRequestDto(senderPhoneNumber = phoneNumber)
            val response = emergencyMessageService.sendEmergencyMessage(request)
            
            // DTO'yu domain model'e çevir
            val domainResponse = response.toDomain()
            
            Result.success(domainResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
