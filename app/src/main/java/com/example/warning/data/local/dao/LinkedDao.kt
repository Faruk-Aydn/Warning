package com.example.warning.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.warning.data.local.entity.LinkedEntity

interface LinkedDao {

    @Query("SELECT * FROM linked")
    suspend fun getAllLinked(): List<LinkedEntity>

    @Delete
    fun deleteLinked(linkedEntity: LinkedEntity)

    @Query("DELETE FROM linked")
    suspend fun deleteAllLinked()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinked(linkedEntity: LinkedEntity)
}