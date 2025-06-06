package com.example.warning.data.mapper

import androidx.room.TypeConverter
import com.example.warning.data.local.entity.SyncType


class Converters {

    @TypeConverter
    fun fromSyncType(value: SyncType): String {
        return value.name // enum ismini stringe çevir
    }

    @TypeConverter
    fun toSyncType(value: String): SyncType {
        return SyncType.valueOf(value) // stringi enum'a çevir
    }
}