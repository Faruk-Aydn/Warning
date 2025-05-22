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

    // Contact işlemleri
    suspend fun getApprovedContacts(): List<Contact> {
        return repository.getApprovedContacts()
    }

    suspend fun insertContact(contact: Contact) {
        repository.insertContact(contact)
    }

    suspend fun insertContacts(contacts: List<Contact>) {
        repository.insertContacts(contacts)
    }

    suspend fun deleteContact(contact: Contact) {
        repository.deleteContact(contact)
    }

    suspend fun deleteAllContacts() {
        repository.deleteAllContacts()
    }
}