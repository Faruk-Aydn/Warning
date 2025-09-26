package com.example.warning.data.remote.Service

import android.util.Log
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    //table Linked

    //tableContact


    suspend fun updateUsername(userId: String, newName: String) {
        firestore.collection("profiles")
            .document(userId)
            .update("name", newName)
            .await()
    }
    // table User
    fun uploadUser(userDto: UserDto?, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (userDto == null) {
            onError(IllegalArgumentException("userDto is null"))
            return
        }

        firestore.collection("profile")
            .document(userDto.phoneNumber)
            .set(userDto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
    suspend fun isUserRegistered(phoneNumber: String): Boolean {
        val snapshot = firestore.collection("profiles")
            .document(phoneNumber)
            .get()
            .await()
        return snapshot.exists()
    }

    suspend fun registerUser(userDto: UserDto) {
        firestore.collection("profiles")
            .document(userDto.phoneNumber)
            .set(userDto)
            .await()
    }

    suspend fun getProfile(phoneNumber: String): UserDto? {
        return try {
            val snapshot = firestore.collection("profiles")
                .document(phoneNumber)
                .get()
                .await()

            Log.e("GetProfile", "Firebase'den veri çekildi: ${snapshot.data}")
            snapshot.toObject(UserDto::class.java)
        } catch (e: Exception) {
            Log.e("GetProfile", "HATA: ${e.message}", e)
            null
        }
    }

    suspend fun getContact(id: String): ContactDto? {
        val snapshot = firestore.collection("profiles")
            .document(id)
            .get()
            .await()
        return snapshot.toObject(ContactDto::class.java)
    }
}
