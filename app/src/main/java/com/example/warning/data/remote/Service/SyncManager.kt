package com.example.warning.data.remote.Service

import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.PendingSyncDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.SyncType
import com.example.warning.data.mapper.toDTO
import com.example.warning.data.network.ConnectivityObserver
import com.example.warning.data.remote.Dto.UserDto
import kotlinx.coroutines.runBlocking

class SyncManager(
    private val dao: ProfileDao,
    private val contactDao: ContactDao,
    private val syncDao: PendingSyncDao,
    private val firestoreService: FirestoreService,
    private val connectivityObserver: ConnectivityObserver
) {

    suspend fun startSync() {
        if (connectivityObserver.isOnline()) {
            val c_dao: List<ContactEntity?>   = contactDao.getAllContacts().map { it }
            val userDto : UserDto? = dao.getProfile()?.toDTO(c_dao)
            val pendingList = syncDao.getAllSyncRequests()
            for (item in pendingList) {
                when (item.syncType) {
                    SyncType.PROFILE_UPDATE -> {

                        firestoreService.uploadUser(
                            userDto = userDto,
                            onSuccess = { runBlocking { syncDao.deleteSyncRequest(item.id) } },
                            onError = { /* logla ama silme */ }
                        )
                    }
                    // Diğer senkron türleri buraya
                }
            }
        }
    }
}
