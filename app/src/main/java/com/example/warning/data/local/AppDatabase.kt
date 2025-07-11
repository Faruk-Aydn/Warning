package com.example.warning.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.PendingSyncEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.mapper.Converters

@Database(
    entities = [
        ProfileEntity::class,
        PendingSyncEntity::class,
        ContactEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun contactDao(): ContactDao
    abstract fun pendingSyncDao(): PendingSyncDao
    abstract fun linkedDao(): LinkedDao
}
