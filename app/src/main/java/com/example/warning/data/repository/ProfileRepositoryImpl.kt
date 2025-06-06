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
import kotlin.text.insert
import android.util.Log

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao,
    private val pendingSyncDao: PendingSyncDao
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
}

