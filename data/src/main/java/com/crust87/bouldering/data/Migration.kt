package com.crust87.bouldering.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `comment` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `text` TEXT NOT NULL, `boulderingId` INTEGER NOT NULL)")
        }
    }
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `comment` ADD COLUMN `createdAt` INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        }
    }

    fun asArray() = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )
}