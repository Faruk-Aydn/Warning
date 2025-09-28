package com.example.warning.domain.repository

import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository{

    //Dao => localden canlı çeker
    suspend fun getMyProfile(): Flow<Profile>
    suspend fun getAllLinked(): Flow<List<Linked>>
    suspend fun getAllContact(): Flow<List<Contact>>
    suspend fun insertProfile(profileEntity: ProfileEntity)
}
