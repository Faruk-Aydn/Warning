package com.example.warning.domain.usecase


import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.ProfileRepository

class ProfileUseCases(
    private val repository: ProfileRepository
) {

    // Profile işlemleri
    suspend fun getProfile(): Profile? {
        return repository.getProfile()
    }

    suspend fun updateProfile(profile: Profile){
        repository.updateProfile(profile)
    }
    suspend fun deleteProfile(profile: Profile){
        repository.deleteProfile()
    }
    suspend fun getAllContact(){
        repository.getAllContacts()
    }
    suspend fun getContactByPhoneNumber(number: String) : Contact? {
        return repository.getContactByPhone(phone = number)
    }
    suspend fun deleteContact(contact: Contact){
        repository.deleteContact(contact)
    }
    suspend fun deleteAllContact(){
        repository.deleteAllContact()
    }
}