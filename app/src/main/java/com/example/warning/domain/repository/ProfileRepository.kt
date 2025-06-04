package com.example.warning.domain.repository

import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile


interface ProfileRepository{
    suspend fun getProfile(): Profile?
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile(profile: Profile)
    suspend fun getAllContacts(): List<Contact?>
    suspend fun getContactByPhone(phone: String): Contact?
    suspend fun deleteAllContact()
    suspend fun deleteContact(contact: Contact)

}
