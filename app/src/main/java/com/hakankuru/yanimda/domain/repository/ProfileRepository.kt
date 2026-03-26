package com.hakankuru.yanimda.domain.repository

import com.hakankuru.yanimda.data.local.entity.ContactEntity
import com.hakankuru.yanimda.data.local.entity.ProfileEntity
import com.hakankuru.yanimda.domain.model.Contact
import com.hakankuru.yanimda.domain.model.Linked
import com.hakankuru.yanimda.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository{

    //Dao => localden canlı çeker
    suspend fun getMyProfile(): Flow<Profile?>
    suspend fun getAllLinked(): Flow<List<Linked>?>
    suspend fun getAllContact(): Flow<List<Contact>?>
    fun insertProfile(profileEntity: ProfileEntity)
    suspend fun insertContact(contactEntity: ContactEntity)
    suspend fun getCurrentUserOnce(): Profile?
    suspend fun getContactOnce(): List<Contact>
    suspend fun clearAllData()
}