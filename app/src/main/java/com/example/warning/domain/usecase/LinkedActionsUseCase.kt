package com.example.warning.domain.usecase

import com.example.warning.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

sealed class LinkedActionResult {
    object Idle : LinkedActionResult()
    object Loading : LinkedActionResult()
    object Success : LinkedActionResult()
    data class Error(val message: String) : LinkedActionResult()
}

class LinkedActionsUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    suspend fun accept(contactId: String): Flow<LinkedActionResult> = flow {
        emit(LinkedActionResult.Loading)
        val ok = firebaseRepository.confirmLinked(contactId)
        if (!ok) {
            emit(LinkedActionResult.Error("İstek kabul edilemedi"))
            return@flow
        }
        emit(LinkedActionResult.Success)
    }

    suspend fun delete(contactId: String): Flow<LinkedActionResult> = flow {
        emit(LinkedActionResult.Loading)
        val ok = firebaseRepository.deleteLinked(contactId)
        if (!ok) {
            emit(LinkedActionResult.Error("Kayıt silinemedi"))
            return@flow
        }
        emit(LinkedActionResult.Success)
    }
}


