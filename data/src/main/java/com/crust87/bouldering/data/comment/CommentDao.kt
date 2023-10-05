package com.crust87.bouldering.data.comment

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.crust87.bouldering.data.COMMENT_PAGE_LIMIT
import com.crust87.bouldering.data.comment.type.Comment
import org.jetbrains.annotations.TestOnly

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg comment: Comment): List<Long>

    @Delete
    suspend fun delete(comment: Comment)

    @Query("DELETE FROM comment WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM comment WHERE boulderingId = :boulderingId")
    suspend fun deleteByBoulderingId(boulderingId: Long)

    @Query("SELECT * FROM comment WHERE boulderingId = :boulderingId")
    suspend fun getAllByBoulderingId(boulderingId: Long): List<Comment>

    @Query("SELECT * FROM comment WHERE boulderingId = :boulderingId ORDER BY id DESC LIMIT 10 OFFSET :page * $COMMENT_PAGE_LIMIT")
    suspend fun getAllByBoulderingId(boulderingId: Long, page: Int): List<Comment>

    @TestOnly
    @Query("SELECT * FROM comment WHERE id = :id")
    suspend fun get(id: Long): Comment?
}