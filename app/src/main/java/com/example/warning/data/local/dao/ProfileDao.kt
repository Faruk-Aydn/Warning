package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.local.entity.ProfileWithContacts
import com.example.warning.domain.model.Contact

@Dao
interface ProfileDao {

    @Transaction
    @Query("SELECT * FROM profile")
    suspend fun getProfile(): ProfileWithContacts?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)


    @Query("DELETE FROM profile")
    suspend fun deleteProfile()

    @Query("DELETE FROM contacts")
    suspend fun clearContacts()
}