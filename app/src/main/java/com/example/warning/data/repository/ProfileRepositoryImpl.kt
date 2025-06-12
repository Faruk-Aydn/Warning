package com.example.warning.data.repository

import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.PendingSyncDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.PendingSyncEntity
import com.example.warning.data.local.entity.SyncType
import com.example.warning.data.mapper.toDomain
import com.example.warning.data.mapper.toEntity
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.ProfileRepository
import android.util.Log
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.mapper.toDTO
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.remote.Service.FirestoreService

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao,
    private val pendingSyncDao: PendingSyncDao,
    private val firestoreService: FirestoreService
) : ProfileRepository {

    val timestamp: Long = System.currentTimeMillis()


    override suspend fun getProfile(): Profile? {
        return profileDao.getProfile()?.toDomain()
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.insertProfile(profile.toEntity())
        Log.d("RegisterViewModel", "Kayıt başarılı: $profile")
        pendingSyncDao.insertSyncRequest(PendingSyncEntity(
            syncType = SyncType.PROFILE_UPDATE,
            timestamp = timestamp
        ))
        Log.d("RegisterViewModel", "Kayıt başarılı: $profile")
        
    }

    override suspend fun deleteProfile() {
        profileDao.deleteProfile()

        // Sync listesine ekle
        if (profileDao != null) {
            pendingSyncDao.insertSyncRequest(
                PendingSyncEntity(
                    syncType = SyncType.PROFILE_UPDATE,
                    timestamp = timestamp
                )
            )
        }
    }

    override suspend fun getAllContacts(): List<Contact?> {
        return  contactDao.getAllContacts().map { it?.toDomain() }
    }

    override suspend fun getContactByPhone(phone: String): Contact? {
        return contactDao.getContactByPhoneNumber(phone)?.toDomain()
    }
    override suspend fun deleteAllContact(){
        contactDao.deleteAllContacts()

        pendingSyncDao.insertSyncRequest(
            PendingSyncEntity(
                syncType = SyncType.PROFILE_UPDATE,
                timestamp = timestamp
            )
        )
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact.toEntity())

        // Sync listesine ekle
        if (contact != null) {
            pendingSyncDao.insertSyncRequest(
                PendingSyncEntity(
                    syncType = SyncType.PROFILE_UPDATE,
                    timestamp = timestamp
                )
            )
        }

    }

    //firebase
    override suspend fun isUserRegistered(phoneNumber: String): Boolean {
        return firestoreService.isUserRegistered(phoneNumber)
    }

    override suspend fun registerUser(profile: ProfileEntity, contact: ContactEntity) {
        // Room'a kaydet
        profileDao.insertProfile(profile)

        // Firestore'a kaydet
        val contacts = contactDao.getAllContacts().map { it }
        val user = profileDao.getProfile()!!.toDTO(contacts)
        firestoreService.registerUser(user)
    }

    override suspend fun getLocalUserDto(): UserDto? {
        val contacts = contactDao.getAllContacts().map { it }
        val user = profileDao.getProfile()!!.toDTO(contacts)
        return user
    }

    override suspend fun getRemoteProfile(phoneNumber: String): UserDto? {
        return firestoreService.getProfile(phoneNumber)
    }
}

