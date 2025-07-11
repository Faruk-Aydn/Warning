package com.example.warning.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.warning.data.local.entity.LinkedEntity
import kotlinx.coroutines.flow.Flow

interface LinkedDao {

    @Query("SELECT * FROM linkeds")
    suspend fun getAllLinked(): Flow<List<LinkedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinked(linkedEntity: List<LinkedEntity?>)
}