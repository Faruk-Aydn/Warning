package com.example.warning.domain.usecase

import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileUseCases @Inject constructor(
    private val repository: ProfileRepository
) {

    // Profile işlemleri
    suspend fun getProfile(phone: String): Flow<Profile> {
        return repository.getMyProfile(phone)
    }

    suspend fun getAllContact(): Flow<List<Contact>> {
        return repository.getAllContact()
    }

    suspend fun getAllLinked(): Flow<List<Linked>>{
        return repository.getAllLinked()
    }

}