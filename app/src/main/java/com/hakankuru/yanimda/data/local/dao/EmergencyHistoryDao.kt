package com.hakankuru.yanimda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hakankuru.yanimda.data.local.entity.IncomingEmergencyEntity
import com.hakankuru.yanimda.data.local.entity.OutgoingEmergencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyHistoryDao {

    // --- INCOMING (GELENLER) ---
    @Query("SELECT * FROM incoming_emergency ORDER BY date DESC")
    fun getAllIncoming(): Flow<List<IncomingEmergencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncoming(entity: IncomingEmergencyEntity)

    @Delete
    suspend fun deleteIncoming(entity: IncomingEmergencyEntity)


    // --- OUTGOING (GİDENLER) ---
    @Query("SELECT * FROM outgoing_emergency ORDER BY date DESC")
    fun getAllOutgoing(): Flow<List<OutgoingEmergencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutgoing(entity: OutgoingEmergencyEntity)

    @Delete
    suspend fun deleteOutgoing(entity: OutgoingEmergencyEntity)

    @Query("DELETE FROM incoming_emergency")
    suspend fun clearIncoming()

    @Query("DELETE FROM outgoing_emergency")
    suspend fun clearOutgoing()
}