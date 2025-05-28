package com.example.warning.domain.repository

import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.domain.model.Contact

// domain/repository/ProfileRepository.kt
import com.example.warning.domain.model.Profile

interface ProfileRepository {

    suspend fun getProfile(): Profile?

    suspend fun updateProfile(profile: Profile)

    suspend fun deleteProfile()

    suspend fun getAllContacts(): List<ContactEntity?>
    suspend fun getContactByPhone(phone: String): Contact?
}
