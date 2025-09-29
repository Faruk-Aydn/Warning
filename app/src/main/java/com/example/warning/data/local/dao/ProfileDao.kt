package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.warning.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Transaction
    @Query("SELECT * FROM profile LIMIT 1")
    fun getCurrentUser(): Flow<ProfileEntity?> // sürekli gözlem


    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getCurrentUserOnce(): ProfileEntity?   // tek seferlik kontrol

    @Query("DELETE FROM profile")
    suspend fun clearProfile()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profile: ProfileEntity)
}