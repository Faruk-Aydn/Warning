package com.example.warning.data.remote.Service

import com.example.warning.data.remote.Dto.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class FirestoreService {

    private val firestore = FirebaseFirestore.getInstance()

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
        val snapshot = firestore.collection("profiles")
            .document(phoneNumber)
            .get()
            .await()

        return snapshot.toObject(UserDto::class.java)
    }
}
