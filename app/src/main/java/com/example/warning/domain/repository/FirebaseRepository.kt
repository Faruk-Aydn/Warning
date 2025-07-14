package com.example.warning.domain.repository

import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.domain.model.Profile

interface FirebaseRepository {


    //Kayıt        => Firebase (succes) -> local
    suspend fun getUser(phone: String): UserDto?
    suspend fun addUser(user: Profile): Boolean
    suspend fun isRegistered(phone: String): Boolean

    /*Listener    ->  firebaseService -> local
      |
      -> Start                                   */
    suspend fun startLinkedListener(phone: String)
    suspend fun startContactListener(phone: String)
    suspend fun startUserListener(phone: String)
//    -> Stop
    suspend fun stopContactListener()
    suspend fun stopUserListener()
    suspend fun stopLinkedListener()
}