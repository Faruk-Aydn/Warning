package com.example.warning.data.repository

import android.util.Log
import androidx.room.Update
import com.example.warning.data.mapper.toDto
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Service.FirestoreService
import com.example.warning.data.remote.listener.ContactRealtimeSyncManager
import com.example.warning.data.remote.listener.LinkedRealtimeSyncManager
import com.example.warning.data.remote.listener.UserRealtimeSyncManager
import com.example.warning.domain.model.Profile
import com.example.warning.domain.repository.FirebaseRepository
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.auth.User
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class FirebaseRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val syncManagerUser: UserRealtimeSyncManager,
    private val syncLinked: LinkedRealtimeSyncManager,
    private val syncContact: ContactRealtimeSyncManager
) : FirebaseRepository {

    override suspend fun getUser(phone: String): UserDto?{
        return firestoreService.getProfile(phone)
    }

    override suspend fun addUser(user: Profile): Boolean {
        return try {
            firestoreService.registerUser(user.toDto())
        }catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e // iptali tekrar yukarı at
        }catch (e: Exception){
            Log.w("Firestore Service","addUser fun'da bir problemden dolayı fale döndü ${e}")
            false
        }
    }

    override suspend fun isRegistered(phone: String): Boolean {
        return firestoreService.isUserRegistered(phone)
    }

    override suspend fun addContact(contact: ContactDto): Boolean {
        return try {
            firestoreService.addContact(contact)
        } catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e
        } catch (e: Exception) {
            Log.w("Firestore Service","addContact hata: ${e}")
            false
        }
    }

    override suspend fun setContactTop(ownerPhone: String, contactPhone: String, isTop: Boolean): Boolean {
        return try {
            firestoreService.updateContactFields(ownerPhone, contactPhone, mapOf("isTop" to isTop))
        } catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e
        } catch (e: Exception) {
            Log.w("Firestore Service","setContactTop hata: ${e}")
            false
        }
    }

    override suspend fun deleteContact(ownerPhone: String, contactPhone: String): Boolean {
        return try {
            firestoreService.deleteContactByOwnerAndPhone(ownerPhone, contactPhone)
        } catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e
        } catch (e: Exception) {
            Log.w("Firestore Service","deleteContact hata: ${e}")
            false
        }
    }

    override suspend fun confirmLinked(contactId: String): Boolean {
        return try {
            firestoreService.updateContactById(contactId, mapOf("isConfirmed" to true))
        } catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e
        } catch (e: Exception) {
            Log.w("Firestore Service","confirmLinked hata: ${e}")
            false
        }
    }

    override suspend fun deleteLinked(contactId: String): Boolean {
        return try {
            firestoreService.deleteContactById(contactId)
        } catch (e: CancellationException) {
            Log.w("Firestore Service","Coroutine iptal edildi: $e")
            throw e
        } catch (e: Exception) {
            Log.w("Firestore Service","deleteLinked hata: ${e}")
            false
        }
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
    override fun stopContactListener(){
        syncContact.stopListening()
    }
    override fun stopUserListener(){
        syncManagerUser.stopListening()
    }
    override fun stopLinkedListener(){
        syncLinked.stopListening()
    }
}