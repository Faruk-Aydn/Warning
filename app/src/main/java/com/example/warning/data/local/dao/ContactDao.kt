package com.example.warning.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.warning.data.local.entity.ContactEntity

@Dao
interface ContactDao {

    // Tüm Contact'ları getir
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<ContactEntity?>

    // Tek bir Contact ekle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    // Tek bir Contact sil
    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    // Tüm Contact'ları sil
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    // Belirli bir Contact'ı getir
    @Query("SELECT * FROM contacts WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getContactByPhoneNumber(phoneNumber: String): ContactEntity?

}