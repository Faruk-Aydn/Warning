package com.example.warning.domain.repository

import com.example.warning.domain.model.Contact

// domain/repository/ProfileRepository.kt
import com.example.warning.domain.model.Profile

interface ProfileRepository {

    suspend fun getProfile(): Profile?

    suspend fun updateProfile(profile: Profile)

    suspend fun deleteProfile()

    suspend fun getApprovedContacts(): List<Contact>

    suspend fun insertContact(contact: Contact)

    suspend fun insertContacts(contacts: List<Contact>)

    suspend fun deleteContact(contact: Contact)

    suspend fun deleteAllContacts()
}
