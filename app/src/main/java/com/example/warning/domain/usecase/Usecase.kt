package com.example.warning.domain.usecase


import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.ProfileRepository

class ProfileUseCases(private val repository: ProfileRepository) {

    // Profile işlemleri
    suspend fun getProfile(): Profile? {
        return repository.getProfile()
    }

    suspend fun insertOrUpdateProfile(profile: Profile) {
        repository.updateProfile(profile)
    }

    suspend fun deleteProfile() {
        repository.deleteProfile()
    }
}