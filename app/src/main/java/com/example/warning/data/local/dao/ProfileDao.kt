package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.domain.model.Contact

@Dao
interface ProfileDao {

    // Profile işlemleri
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profile LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile()
}