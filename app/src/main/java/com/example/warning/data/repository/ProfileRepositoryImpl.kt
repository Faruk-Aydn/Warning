package com.example.warning.data.repository

import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toDomain
import com.example.warning.data.mapper.toEntity
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao
) : ProfileRepository {

    override suspend fun getProfile(): Profile? {
        return profileDao.getProfile()?.toDomain()
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.insertProfile(profile.toEntity())
    }

    override suspend fun deleteProfile() {
        profileDao.deleteProfile()
    }

    override suspend fun getAllContacts(): List<Contact?> {
        return  contactDao.getAllContacts().map { it?.toDomain() }
    }

    override suspend fun getContactByPhone(phone: String): Contact? {
        return contactDao.getContactByPhoneNumber(phone)?.toDomain()
    }
    override suspend fun deleteAllContact(){
        contactDao.deleteAllContacts()
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact.toEntity())
    }

}