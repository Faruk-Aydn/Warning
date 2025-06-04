package com.example.warning.data.remote.Service

import com.example.warning.data.local.dao.PendingSyncDao
import com.example.warning.data.local.dao.ProfileDao

class SyncManager(
    private val dao: ProfileDao,
    private val syncDao: PendingSyncDao,
    private val firestoreService: FirestoreService
) {

    suspend fun syncPendingItems() {
        val pendingItems = syncDao.getAllSyncRequests()

        for (item in pendingItems) {
            val profileWithContacts = dao.getProfileWithContacts(item.phone)
            val userDto = profileWithContacts.toDto()

            firestoreService.uploadUser(
                userDto,
                onSuccess = {
                    syncDao.deleteSyncRequest(id = item.id)  // senkron başarılıysa sil
                },
                onError = {
                    // isteğe bağlı: loglama
                }
            )
        }
    }
}
