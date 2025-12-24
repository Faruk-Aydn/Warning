package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.warning.data.local.entity.LinkedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkedDao {

    @Query("SELECT * FROM linkeds")
    fun getAllLinked(): Flow<List<LinkedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLinked(linkedEntity: List<LinkedEntity>)

    @Query("DELETE FROM linkeds")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(linkedEntity: List<LinkedEntity>) {
        clearAll()
        if (linkedEntity.isNotEmpty()) {
            insertLinked(linkedEntity)
        }
    }
}