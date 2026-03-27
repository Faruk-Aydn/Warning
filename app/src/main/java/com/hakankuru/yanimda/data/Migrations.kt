package com.hakankuru.yanimda.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        // outgoing_emergency tablosuna latitude/longitude sütunları ekle
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN latitude REAL")
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN longitude REAL")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // incoming_emergency tablosu ilk kez oluşturuluyor
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS incoming_emergency (
                id TEXT NOT NULL PRIMARY KEY,
                senderId TEXT NOT NULL,
                senderName TEXT,
                messageContent TEXT NOT NULL,
                latitude REAL,
                longitude REAL,
                date INTEGER NOT NULL
            )
        """.trimIndent())
        // outgoing_emergency tablosu ilk kez oluşturuluyor
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS outgoing_emergency (
                id TEXT NOT NULL PRIMARY KEY,
                receiverId TEXT NOT NULL,
                receiverName TEXT NOT NULL,
                messageContent TEXT NOT NULL,
                isLocationSent INTEGER NOT NULL,
                latitude REAL,
                longitude REAL,
                status TEXT NOT NULL,
                success INTEGER NOT NULL,
                error TEXT,
                date INTEGER NOT NULL
            )
        """.trimIndent())
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // incoming_emergency tablosuna isLocationSent sütunu ekleniyor
        database.execSQL("ALTER TABLE incoming_emergency ADD COLUMN isLocationSent INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // incoming_emergency tablosuna yeni alanlar
        database.execSQL("ALTER TABLE incoming_emergency ADD COLUMN senderPhone TEXT")
        database.execSQL("ALTER TABLE incoming_emergency ADD COLUMN senderCountry TEXT")
        database.execSQL("ALTER TABLE incoming_emergency ADD COLUMN receiverPhone TEXT")
        database.execSQL("ALTER TABLE incoming_emergency ADD COLUMN receiverCountry TEXT")

        // outgoing_emergency tablosuna yeni alanlar
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN senderPhone TEXT")
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN senderCountry TEXT")
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN receiverPhone TEXT")
        database.execSQL("ALTER TABLE outgoing_emergency ADD COLUMN receiverCountry TEXT")
    }
}