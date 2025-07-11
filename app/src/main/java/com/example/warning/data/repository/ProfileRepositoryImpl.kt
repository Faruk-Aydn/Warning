package com.example.warning.data.repository

import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toDomain
import com.example.warning.data.mapper.toDto
import com.example.warning.data.mapper.toEntity
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
    private val contactDao: ContactDao,
    private val firestoreService: FirestoreService,
    private val syncManager: UserRealtimeSyncManager,
    private val syncLinked: LinkedRealtimeSyncManager,
    private val syncContact: ContactRealtimeSyncManager
) : ProfileRepository {
    override suspend fun getMyProfile(id: String): Flow<Profile> {
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

    override suspend fun addUser(user: Profile): Boolean {
        try {
            firestoreService.registerUser(user.toDto())
        }catch (e: FirebaseException){
            return false
        }
        catch (e: Exception){
            return false
        }
        return true
    }

    override suspend fun isRegistered(phone: String): Boolean {
        return firestoreService.isUserRegistered(phone)
    }

    //Start
    override suspend fun startContactListener(phone: String){
        syncContact.startListening(phone)
    }
    override suspend fun startUserListener(phone: String){
        syncManager.startListening(phone)
    }
    override suspend fun startLinkedListener(phone: String){
        syncLinked.startListening(phone)
    }

    //Stop
    override suspend fun stopContactListener(phone: String){
        syncContact.stopListening()
    }
    override suspend fun stoptUserListener(phone: String){
        syncManager.stopListening()
    }
    override suspend fun stopLinkedListener(phone: String){
        syncLinked.stopListening()
    }
}

