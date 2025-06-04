package com.example.warning.data.remote.Service

import com.example.warning.data.remote.Dto.UserDto
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService {

    private val firestore = FirebaseFirestore.getInstance()

    fun uploadUser(userDto: UserDto, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("users")
            .document(userDto.phoneNumber)
            .set(userDto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
