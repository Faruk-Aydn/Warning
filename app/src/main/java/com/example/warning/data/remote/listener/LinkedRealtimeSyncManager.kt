package com.example.warning.data.remote.listener

import android.util.Log
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.mapper.toEntity
import com.example.warning.data.mapper.toLinked
import com.example.warning.data.remote.Dto.ContactDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.java


class LinkedRealtimeSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val linkedDao: LinkedDao
) {

    private var listener: ListenerRegistration? = null

    fun startListening(phone: String) {
        if (listener != null) return // zaten çalışıyorsa tekrar başlatma

        listener = firestore.collection("contacts")
            .whereEqualTo("phone", phone)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("LinkedSync", "Dinleme hatası", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val linkedList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ContactDto::class.java)?.let { dto ->
                            dto.toLinked().toEntity()
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        linkedDao.insertLinked(linkedList)

                    }
                }
            }
        Log.i("LinkedSync", "LinkedSync dinleyici başlatıldı: $phone")
    }



    fun stopListening() {
        listener?.remove()
        listener = null
        Log.d("LinkedSync", "Linked dinleyici durduruldu")
    }

    fun isListening(): Boolean = listener != null
}
