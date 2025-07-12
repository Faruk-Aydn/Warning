package com.example.warning.domain.repository

import com.example.warning.domain.model.Profile

interface FirebaseRepository {


    //Kayıt        => Firebase (succes) -> local
    suspend fun addUser(user: Profile): Boolean
    suspend fun isRegistered(phone: String): Boolean

    //Listener    ->  firebaseService -> local
    suspend fun startLinkedListener(phone: String)
    suspend fun startContactListener(phone: String)
    suspend fun startUserListener(phone: String)
    suspend fun stopContactListener(phone: String)
    suspend fun stoptUserListener(phone: String)
    suspend fun stopLinkedListener(phone: String)
}