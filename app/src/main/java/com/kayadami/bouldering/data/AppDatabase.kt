package com.kayadami.bouldering.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kayadami.bouldering.data.bouldering.BoulderingDao
import com.kayadami.bouldering.data.bouldering.HolderListConverters
import com.kayadami.bouldering.data.bouldering.type.BoulderingEntity
import com.kayadami.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.data.comment.type.Comment

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