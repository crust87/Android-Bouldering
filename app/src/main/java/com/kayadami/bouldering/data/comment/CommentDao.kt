package com.kayadami.bouldering.data.comment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kayadami.bouldering.data.comment.type.Comment
import org.jetbrains.annotations.TestOnly

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg comment: Comment): List<Long>

    @Delete
    fun delete(comment: Comment)

    @Query("DELETE FROM comment WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM comment WHERE boulderingId = :boulderingId")
    fun getAllByBoulderingId(boulderingId: Long): List<Comment>

    @TestOnly
    @Query("SELECT * FROM comment WHERE id = :id")
    fun get(id: Long): Comment?
}