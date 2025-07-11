package com.example.warning.domain.usecase

import com.example.warning.domain.repository.ProfileRepository
import javax.inject.Inject

class ListenerUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun startUserListener(phone: String){
        repository.startContactListener(phone)
    }
    suspend fun startLinkedListener(phone: String){
        repository.startContactListener(phone)
    }
    suspend fun stopUserListener(phone: String){
        repository.startContactListener(phone)
    }
    suspend fun stopContactListener(phone: String){
        repository.startContactListener(phone)
    }
    suspend fun stopLinkedListener(phone: String){
        repository.startContactListener(phone)
    }
}