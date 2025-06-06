package com.example.warning.data.remote.Service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.warning.data.local.dao.PendingSyncDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.mapper.toDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncManager(
    private val dao: ProfileDao,
    private val syncDao: PendingSyncDao,
    private val firestoreService: FirestoreService
) {

    suspend fun syncPendingItems() {
        val pendingItems = syncDao.getAllSyncRequests()

        for (item in pendingItems) {
            val profileWithContacts = dao.getProfileWithContacts()
            val userDto = profileWithContacts.toDto()

            firestoreService.uploadUser(
                userDto,
                onSuccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        syncDao.deleteSyncRequest(id = item.id)
                        Log.d(TAG, "Sync success for item id: ${item.id}")
                    }
                },
                onError = {
                    Log.e(TAG, "Sync failed for item id: ${item.id}, error: $it")
                    // isteğe bağlı: loglama
                }
            )
        }
    }
}
