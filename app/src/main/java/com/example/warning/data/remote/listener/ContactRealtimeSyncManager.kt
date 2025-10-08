package com.example.warning.data.remote.listener

import android.util.Log
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.mapper.toEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.google.firebase.firestore.DocumentChange
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
                    Log.w("ContactSync", "Dinleme hatası", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        snapshot.documentChanges.forEach { change ->
                            val dto = change.document.toObject(ContactDto::class.java).apply { id = change.document.id }
                            val contact = dto.toEntity()
                            when (change.type) {
                                DocumentChange.Type.ADDED -> {
                                    contactDao.insertContact(listOf(contact))
                                    Log.i("ContactSync", "Contact eklendi: ${contact.phone} to ${contact.ownerPhone}")
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    contactDao.updateContact(contact)
                                    Log.i("ContactSync", "Contact güncellendi: ${contact.phone} to ${contact.ownerPhone}")
                                }
                                DocumentChange.Type.REMOVED -> {
                                    contactDao.deleteContact(contact)
                                    Log.i("ContactSync", "Contact silindi: ${contact.phone} to ${contact.ownerPhone}")
                                }
                            }
                        }
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
