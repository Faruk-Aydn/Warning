package com.example.warning.data.remote.listener

import android.util.Log
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.remote.Service.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRealtimeSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val profileDao: ProfileDao
){
    private var listenerRegistration: ListenerRegistration? = null

    fun startListening(phoneNumber: String) {
        if (listenerRegistration != null) return // Zaten dinliyorsa bir daha başlatma

        listenerRegistration = firestore.collection("profiles")
            .document(phoneNumber)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserRealtimeSync", "Dinleme hatası", error)
                    return@addSnapshotListener
                }

                snapshot?.toObject(UserDto::class.java)?.let { dto ->
                    CoroutineScope(Dispatchers.IO).launch {
                        profileDao.insertProfile(dto.toEntity())

                    }
                }
            }

        Log.d("UserRealtimeSync", "Dinleme başlatıldı: $phoneNumber")
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
        Log.d("UserRealtimeSync", "Dinleme durduruldu")
    }

    fun isListening(): Boolean = listenerRegistration != null
}