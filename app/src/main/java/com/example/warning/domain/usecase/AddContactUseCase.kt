package com.example.warning.domain.usecase

import com.example.warning.data.mapper.toEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

sealed class AddContactResult {
    object Idle : AddContactResult()
    object Loading : AddContactResult()
    data class NotFound(val message: String = "Kullanıcı bulunamadı") : AddContactResult()
    object Success : AddContactResult()
    data class Error(val message: String) : AddContactResult()
}

class AddContactUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(phone: String, country: String): Flow<AddContactResult> = flow {
        emit(AddContactResult.Loading)

        val exists = firebaseRepository.isRegistered(phone)
        if (!exists) {
            emit(AddContactResult.NotFound())
            return@flow
        }

        val owner = profileRepository.getCurrentUserOnce()
        if (owner == null) {
            emit(AddContactResult.Error("Mevcut kullanıcı bulunamadı"))
            return@flow
        }

        val contactId = UUID.randomUUID().toString()
        val dto = ContactDto(
            id = contactId,
            phone = phone,
            country = country,
            name = owner.name,
            profilePhoto = null,
            ownerProfilePhoto = owner.profilePhoto,
            ownerPhone = owner.phoneNumber,
            ownerCountry = owner.country,
            ownerName = owner.name,
            isActiveUser = true,
            specialMessage = null,
            isLocationSend = false,
            tag = null,
            isTop = false,
            isConfirmed = false,
            date = System.currentTimeMillis()
        )

        val remoteOk = firebaseRepository.addContact(dto)
        if (!remoteOk) {
            emit(AddContactResult.Error("Sunucuya kaydedilemedi"))
            return@flow
        }

        profileRepository.insertContact(dto.toEntity())
        emit(AddContactResult.Success)
    }
}


