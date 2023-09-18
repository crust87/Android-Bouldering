package com.kayadami.bouldering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kayadami.bouldering.data.type.Bouldering

@Database(entities = [Bouldering::class], version = 1)
@TypeConverters(HolderListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boulderingDao(): BoulderingDao
}