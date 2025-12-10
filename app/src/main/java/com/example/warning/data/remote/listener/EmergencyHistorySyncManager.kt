package com.example.warning.data.remote.listener

import android.util.Log
import com.example.warning.data.local.dao.EmergencyHistoryDao
import com.example.warning.data.mapper.toIncomingEntity
import com.example.warning.data.mapper.toOutgoingEntity
import com.example.warning.data.remote.Dto.EmergencyHistoryDto
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmergencyHistorySyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dao: EmergencyHistoryDao
) {
    private var sentListener: ListenerRegistration? = null
    private var receivedListener: ListenerRegistration? = null

    fun startListening(userId: String) {
        if (userId.isBlank()) return
        Log.i("EmergencySync", "Sync başlatılıyor. UserID: $userId")

        // 1. GÖNDERDİKLERİM (Outgoing Tablosuna Yazılacak)
        if (sentListener == null) {
            sentListener = firestore.collection("emergency_history")
                .whereEqualTo("senderId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    snapshot?.documentChanges?.forEach { change ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val dto = change.document.toObject(EmergencyHistoryDto::class.java).apply {
                                    id = change.document.id
                                }
                                // Outgoing Entity'e çevir ve kaydet
                                val entity = dto.toOutgoingEntity()

                                when (change.type) {
                                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED ->
                                        dao.insertOutgoing(entity)
                                    DocumentChange.Type.REMOVED ->
                                        dao.deleteOutgoing(entity)
                                }
                            } catch (e: Exception) {
                                Log.e("EmergencySync", "Outgoing save error: ${e.message}")
                            }
                        }
                    }
                }
        }

        // 2. GELENLER (Incoming Tablosuna Yazılacak)
        if (receivedListener == null) {
            receivedListener = firestore.collection("emergency_history")
                .whereEqualTo("receiverId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    snapshot?.documentChanges?.forEach { change ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val dto = change.document.toObject(EmergencyHistoryDto::class.java).apply {
                                    id = change.document.id
                                }
                                // Incoming Entity'e çevir ve kaydet
                                val entity = dto.toIncomingEntity()

                                when (change.type) {
                                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED ->
                                        dao.insertIncoming(entity)
                                    DocumentChange.Type.REMOVED ->
                                        dao.deleteIncoming(entity)
                                }
                            } catch (e: Exception) {
                                Log.e("EmergencySync", "Incoming save error: ${e.message}")
                            }
                        }
                    }
                }
        }
    }

    fun stopListening() {
        sentListener?.remove()
        receivedListener?.remove()
        sentListener = null
        receivedListener = null
    }
}