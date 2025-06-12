package com.example.warning.domain.repository

import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile


interface ProfileRepository{
    suspend fun getProfile(): Profile?
    suspend fun updateProfile(profile: Profile)
    suspend fun deleteProfile()
    suspend fun getAllContacts(): List<Contact?>
    suspend fun getContactByPhone(phone: String): Contact?
    suspend fun deleteAllContact()
    suspend fun deleteContact(contact: Contact)
    suspend fun isUserRegistered(phoneNumber: String): Boolean
    suspend fun registerUser(profile: ProfileEntity, contact: ContactEntity)
    suspend fun getLocalUserDto(): UserDto?
    suspend fun getRemoteProfile(phoneNumber: String): UserDto?
}
