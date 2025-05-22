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
        return profileDao.getProfile()?.toDomain()?.copy(
            contacts = contactDao.getAllContacts().map { it.toDomain() }
        )
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.insertProfile(profile.toEntity())
        contactDao.deleteAllContacts()
        contactDao.insertContacts(profile.contacts.map { it.toEntity() })
    }

    override suspend fun deleteProfile() {
        profileDao.deleteProfile()
        contactDao.deleteAllContacts()
    }

    override suspend fun getApprovedContacts(): List<Contact> =
        contactDao.getAllContacts().map { it.toDomain() }

    override suspend fun insertContact(contact: Contact) =
        contactDao.insertContact(contact.toEntity())

    override suspend fun insertContacts(contacts: List<Contact>) =
        contactDao.insertContacts(contacts.map { it.toEntity() })

    override suspend fun deleteContact(contact: Contact) =
        contactDao.deleteContact(contact.toEntity())

    override suspend fun deleteAllContacts() = contactDao.deleteAllContacts()
}