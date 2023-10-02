package com.kayadami.bouldering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kayadami.bouldering.data.bouldering.BoulderingDao
import com.kayadami.bouldering.data.bouldering.HolderListConverters
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.comment.type.Comment

@Database(
    version = 3,
    entities = [
        Bouldering::class,
        Comment::class
    ],
)
@TypeConverters(HolderListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boulderingDao(): BoulderingDao
}