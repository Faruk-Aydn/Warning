package com.example.warning.domain.usecase

import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

sealed class ContactActionResult {
    object Idle : ContactActionResult()
    object Loading : ContactActionResult()
    object Success : ContactActionResult()
    data class Error(val message: String) : ContactActionResult()
}

class ContactActionsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val profileRepository: ProfileRepository
) {
    suspend fun toggleTop(contactPhone: String, makeTop: Boolean): Flow<ContactActionResult> = flow {
        emit(ContactActionResult.Loading)
        val owner = profileRepository.getCurrentUserOnce()
        if (owner == null) {
            emit(ContactActionResult.Error("Mevcut kullanıcı bulunamadı"))
            return@flow
        }
        val ok = firebaseRepository.setContactTop(owner.phoneNumber, contactPhone, makeTop)
        if (!ok) {
            emit(ContactActionResult.Error("Favori güncellenemedi"))
            return@flow
        }
        emit(ContactActionResult.Success)
    }

    suspend fun delete(contactPhone: String): Flow<ContactActionResult> = flow {
        emit(ContactActionResult.Loading)
        val owner = profileRepository.getCurrentUserOnce()
        if (owner == null) {
            emit(ContactActionResult.Error("Mevcut kullanıcı bulunamadı"))
            return@flow
        }
        val ok = firebaseRepository.deleteContact(owner.phoneNumber, contactPhone)
        if (!ok) {
            emit(ContactActionResult.Error("Bağlantı silinemedi"))
            return@flow
        }
        emit(ContactActionResult.Success)
    }
}


