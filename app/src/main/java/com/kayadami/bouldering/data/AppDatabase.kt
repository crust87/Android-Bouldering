package com.kayadami.bouldering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kayadami.bouldering.data.type.Bouldering
import com.kayadami.bouldering.data.type.Comment

@Database(
    version = 2,
    entities = [
        Bouldering::class,
        Comment::class
    ],
)
@TypeConverters(HolderListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boulderingDao(): BoulderingDao
}