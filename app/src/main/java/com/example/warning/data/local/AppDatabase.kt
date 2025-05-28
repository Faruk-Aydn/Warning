package com.example.warning.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity

@Database(
    entities = [ProfileEntity::class, ContactEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
