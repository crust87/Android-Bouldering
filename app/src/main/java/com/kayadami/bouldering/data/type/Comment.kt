package com.kayadami.bouldering.data.type

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comment")
class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "text")
    var text: String,
    @ColumnInfo(name = "boulderingId")
    var boulderingId: Long,
)