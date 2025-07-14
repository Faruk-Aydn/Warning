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
    @Query("SELECT * FROM profile")
    fun getProfile(): Flow<ProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profile: ProfileEntity)
}