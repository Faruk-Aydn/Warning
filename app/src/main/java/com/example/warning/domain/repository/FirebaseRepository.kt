package com.example.warning.domain.repository

import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.domain.model.Profile

interface FirebaseRepository {


    //Kayıt        => Firebase (succes) -> local
    suspend fun getUser(phone: String): UserDto?
    suspend fun addUser(user: Profile): Boolean
    suspend fun addContact(contact: com.example.warning.data.remote.Dto.ContactDto): Boolean
    suspend fun isRegistered(phone: String): Boolean

    // Contact actions (by phone for legacy, by id for new flow)
    suspend fun setContactTop(ownerPhone: String, contactPhone: String, isTop: Boolean): Boolean
    suspend fun deleteContact(ownerPhone: String, contactPhone: String): Boolean
    suspend fun setContactTopById(contactId: String, isTop: Boolean): Boolean
    suspend fun deleteContactById(contactId: String): Boolean

    /**
     * Güncellenen FCM token'ı Firebase'e kaydeder
     */
    suspend fun updateFCMToken(userId: String, token: String): Boolean

    // Linked actions (operate on contact doc id viewed as linked)
    suspend fun confirmLinked(
        contactId: String,
        phone: String,
        country: String,
        name: String
    ): Boolean
    suspend fun deleteLinked(contactId: String): Boolean

    /*Listener    ->  firebaseService -> local
      |
      -> Start                                   */
    suspend fun startLinkedListener(phone: String)
    suspend fun startContactListener(phone: String)
    suspend fun startUserListener(phone: String)
//    -> Stop
    fun stopContactListener()
    fun stopUserListener()
    fun stopLinkedListener()
}