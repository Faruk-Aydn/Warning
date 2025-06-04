package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.warning.data.local.entity.PendingSyncEntity

@Dao
interface PendingSyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncRequest(request: PendingSyncEntity)

    @Query("SELECT * FROM pending_sync")
    suspend fun getAllSyncRequests(): List<PendingSyncEntity>

    @Query("DELETE FROM pending_sync WHERE id = :id")
    suspend fun deleteSyncRequest(id: Int)

    @Query("DELETE FROM pending_sync")
    suspend fun deleteAllSyncRequests()
}
