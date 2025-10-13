package com.example.warning.data.remote.listener

import android.R.attr.phoneNumber
import android.util.Log
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toEntity
import com.example.warning.data.remote.Dto.UserDto
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

    fun startListening(phone: String) {
        if (listenerRegistration != null) return // Zaten dinliyorsa bir daha başlatma

        listenerRegistration = firestore.collection("profiles")
            .whereEqualTo("phoneNumber", phone)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("UserRealtimeSync", "Dinleme hatası", error)
                    return@addSnapshotListener
                }
                snapshot?.documents?.firstOrNull()?.toObject(UserDto::class.java)?.let { dto ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.i("UserRealtimeSync", "Dinleme başlatıldı: ${dto.phoneNumber}")
                        profileDao.insertProfile(dto.toEntity())
                    }
                }

            }

    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
        Log.d("UserRealtimeSync", "Dinleme durduruldu")
    }

    fun isListening(): Boolean = listenerRegistration != null
}