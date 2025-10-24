package com.example.warning.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.warning.data.local.AppDatabase

val MIGRATION_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE user ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
    }
}
val MIGRATION_2_3 = object : Migration(2,3){
    override fun migrate(database: SupportSQLiteDatabase){
        // linked tablosu oluşturuluyor
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS linked (
                phoneNumber TEXT NOT NULL PRIMARY KEY,
                name TEXT,
                nickName TEXT,
                ownerPhoneNumber TEXT NOT NULL,
                date INTEGER NOT NULL,
                FOREIGN KEY(ownerPhoneNumber) REFERENCES profile(phoneNumber) ON DELETE CASCADE
            )
        """.trimIndent())


        // contact tablosuna yeni sütunlar ekleniyor
        database.execSQL("ALTER TABLE contact ADD COLUMN specielMessage TEXT")
        database.execSQL("ALTER TABLE contact ADD COLUMN isLocationSend INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE contact ADD COLUMN tag TEXT")
        database.execSQL("ALTER TABLE contact ADD COLUMN isTop INTEGER NOT NULL DEFAULT 0")

        database.execSQL("ALTER TABLE profile ADD COLUMN locationPermission INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE profile ADD COLUMN ContactPermission INTEGER NOT NULL DEFAULT 0")

    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // profile tablosuna fcmToken sütununu ekle
        database.execSQL("ALTER TABLE profile ADD COLUMN fcmToken TEXT")
    }
}
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // profile tablosuna fcmToken sütununu ekle
        database.execSQL("ALTER TABLE contacts DELETE COLUMN fcmToken TEXT")
    }
}