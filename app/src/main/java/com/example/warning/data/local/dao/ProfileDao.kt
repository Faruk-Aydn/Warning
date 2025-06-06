package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.local.entity.ProfileWithContacts

@Dao
interface ProfileDao {

    @Transaction
    @Query("SELECT * FROM profile")
    suspend fun getProfile(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("DELETE FROM profile")
    suspend fun deleteProfile()

    @Transaction
    @Query("SELECT * FROM profile LIMIT 1") // ya da hangi user ise
    suspend fun getProfileWithContacts(): ProfileWithContacts
}