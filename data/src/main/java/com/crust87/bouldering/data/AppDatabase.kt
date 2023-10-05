package com.crust87.bouldering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.crust87.bouldering.data.bouldering.BoulderingDao
import com.crust87.bouldering.data.bouldering.HolderListConverters
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.data.comment.CommentDao
import com.crust87.bouldering.data.comment.type.Comment

@Database(
    version = 3,
    entities = [
        BoulderingEntity::class,
        Comment::class
    ],
)
@TypeConverters(HolderListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boulderingDao(): BoulderingDao

    abstract fun commentDao(): CommentDao
}