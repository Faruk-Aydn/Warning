package com.example.warning.data.repository

import android.util.Log
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.mapper.toDomain
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val linkedDao: LinkedDao,
    private val contactDao: ContactDao
) : ProfileRepository {
    override suspend fun getMyProfile(): Flow<Profile?> {
        return profileDao.getCurrentUser().map { it?.toDomain() }
    }

    override suspend fun getAllLinked(): Flow<List<Linked>?> {
        return linkedDao.getAllLinked().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAllContact(): Flow<List<Contact>?> {
        return contactDao.getAllContacts().map { list ->
            list.map{ it.toDomain() }
        }
    }
    override fun insertProfile(profileEntity: ProfileEntity){
        profileDao.insertProfile(profileEntity)
    }

    override suspend fun insertContact(contactEntity: ContactEntity){
        contactDao.insertContact(listOf(contactEntity))
    }

    override suspend fun getCurrentUserOnce(): Profile? {
        val entity = profileDao.getCurrentUserOnce()
        Log.d("ProfileRepositoryImpl", "getCurrentUserOnce() entity: $entity")
        val domain = entity?.toDomain()
        Log.d("ProfileRepositoryImpl", "getCurrentUserOnce() domain: $domain")
        return domain
    }
}

