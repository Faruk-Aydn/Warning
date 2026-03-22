package com.hakankuru.yanimda.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hakankuru.yanimda.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    // Tüm Contact'ları getir
    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<List<ContactEntity>>


    // Tüm Contact'ları getir
    @Query("SELECT * FROM contacts")
    suspend fun getAllContactsOnce(): List<ContactEntity>

    // Tek bir Contact ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contact: List<ContactEntity>)


    @Update
    suspend fun updateContact(contact: ContactEntity)

    // 3️⃣ Contact silme
    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}