package com.example.warning.data.repository

import androidx.room.Update
import com.example.warning.data.mapper.toDto
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.remote.Service.FirestoreService
import com.example.warning.data.remote.listener.ContactRealtimeSyncManager
import com.example.warning.data.remote.listener.LinkedRealtimeSyncManager
import com.example.warning.data.remote.listener.UserRealtimeSyncManager
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.FirebaseRepository
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.auth.User
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val syncManagerUser: UserRealtimeSyncManager,
    private val syncLinked: LinkedRealtimeSyncManager,
    private val syncContact: ContactRealtimeSyncManager
) : FirebaseRepository {

    override suspend fun getUser(phone: String): UserDto?{
        var user =firestoreService.getProfile(phone)
        if (user == null)
            return null
        else{
            return user
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
        syncManagerUser.startListening(phone)
    }
    override suspend fun startLinkedListener(phone: String){
        syncLinked.startListening(phone)
    }

    //Stop
    override suspend fun stopContactListener(){
        syncContact.stopListening()
    }
    override suspend fun stopUserListener(){
        syncManagerUser.stopListening()
    }
    override suspend fun stopLinkedListener(){
        syncLinked.stopListening()
    }
}