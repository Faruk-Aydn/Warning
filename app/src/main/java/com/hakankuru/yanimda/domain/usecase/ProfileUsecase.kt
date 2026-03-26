package com.hakankuru.yanimda.domain.usecase

import com.hakankuru.yanimda.domain.model.Contact
import com.hakankuru.yanimda.domain.model.Linked
import com.hakankuru.yanimda.domain.model.Profile
import com.hakankuru.yanimda.domain.repository.FirebaseRepository
import com.hakankuru.yanimda.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileUseCases @Inject constructor(
    private val repository: ProfileRepository,
    private val firebaseRepo: FirebaseRepository
) {

    // Profile işlemleri
    suspend fun getProfile(): Flow<Profile?> {
        return repository.getMyProfile()
    }
    suspend fun getProfileOnce(): Profile?{
        return repository.getCurrentUserOnce()
    }

    suspend fun getAllContact(): Flow<List<Contact>?> {
        return repository.getAllContact()
    }

    suspend fun getAllLinked(): Flow<List<Linked>?>{
        return repository.getAllLinked()
    }

    //Start
    suspend fun startContactListener(phone: String){
        firebaseRepo.startContactListener(phone)
    }
    suspend fun startUserListener(phone: String){
        firebaseRepo.startUserListener(phone)
    }
    suspend fun startLinkedListener(phone: String){
        firebaseRepo.startLinkedListener(phone)
    }

    // Stop
    fun stopContactListener(){
        firebaseRepo.stopContactListener()
    }
    fun stopUserListener(){
        firebaseRepo.stopUserListener()
    }
    fun stopLinkedListener(){
        firebaseRepo.stopLinkedListener()
    }
}