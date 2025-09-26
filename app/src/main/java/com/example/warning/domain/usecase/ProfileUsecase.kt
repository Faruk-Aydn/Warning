package com.example.warning.domain.usecase

import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.FirebaseRepository
import com.example.warning.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileUseCases @Inject constructor(
    private val repository: ProfileRepository,
    private val firebaseRepo: FirebaseRepository
) {

    // Profile işlemleri
    suspend fun getProfile(): Flow<Profile> {
        return repository.getMyProfile()
    }

    suspend fun getAllContact(): Flow<List<Contact>> {
        return repository.getAllContact()
    }

    suspend fun getAllLinked(): Flow<List<Linked>>{
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
    suspend fun stopContactListener(){
        firebaseRepo.stopContactListener()
    }
    suspend fun stopUserListener(){
        firebaseRepo.stopUserListener()
    }
    suspend fun stopLinkedListener(){
        firebaseRepo.stopLinkedListener()
    }


}