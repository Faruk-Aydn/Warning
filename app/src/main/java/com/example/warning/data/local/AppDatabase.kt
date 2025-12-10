package com.example.warning.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.warning.data.local.dao.ContactDao
import com.example.warning.data.local.dao.EmergencyHistoryDao
import com.example.warning.data.local.dao.LinkedDao
import com.example.warning.data.local.dao.ProfileDao
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.EmergencyHistoryEntity
import com.example.warning.data.local.entity.IncomingEmergencyEntity
import com.example.warning.data.local.entity.LinkedEntity
import com.example.warning.data.local.entity.OutgoingEmergencyEntity
import com.example.warning.data.local.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        LinkedEntity::class,
        ContactEntity::class,
        IncomingEmergencyEntity::class,
        OutgoingEmergencyEntity::class
               ],
    version = 7
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun contactDao(): ContactDao
    abstract fun linkedDao(): LinkedDao
    abstract fun emergencyHistoryDao(): EmergencyHistoryDao
}
