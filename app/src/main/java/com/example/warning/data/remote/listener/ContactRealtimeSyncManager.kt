package com.example.warning.data.remote.listener

import android.util.Log
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.mapper.toEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactRealtimeSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val contactDao: ContactDao
) {

    private var listener: ListenerRegistration? = null

    fun startListening(ownerPhone: String) {
        if (listener != null) return // zaten çalışıyorsa tekrar başlatma

        listener = firestore.collection("contacts")
            .whereEqualTo("ownerPhone", ownerPhone)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ContactSync", "Dinleme hatası", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val contactList = snapshot.documents.mapNotNull { it ->
                        it.toObject(ContactDto::class.java)?.toEntity()
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        contactDao.insertContact(contactList)
                        Log.i("contactDao,listener",contactList.size.toString())
                    }
                }
            }
        Log.i("ContactSync", "Contact dinleyici başlatıldı: $ownerPhone")
    }



fun stopListening() {
    listener?.remove()
    listener = null
    Log.d("ContactSync", "Contact dinleyici durduruldu")
}

fun isListening(): Boolean = listener != null
}
