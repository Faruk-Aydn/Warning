package com.example.warning.data.repository

import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toDomain
import com.example.warning.data.mapper.toDto
import com.example.warning.domain.repository.ProfileRepository
import com.example.warning.data.remote.Service.FirestoreService
import com.example.warning.data.remote.listener.ContactRealtimeSyncManager
import com.example.warning.data.remote.listener.LinkedRealtimeSyncManager
import com.example.warning.data.remote.listener.UserRealtimeSyncManager
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val linkedDao: LinkedDao,
    private val contactDao: ContactDao
) : ProfileRepository {
    override suspend fun getMyProfile(): Flow<Profile> {
        return profileDao.getProfile().map { it.toDomain()}
    }

    override suspend fun getAllLinked(): Flow<List<Linked>> {
        return linkedDao.getAllLinked().map { list ->
            list.map { it.toDomain()  }
        }
    }

    override suspend fun getAllContact(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { list ->
            list.map{ it.toDomain() }
        }
    }
}

