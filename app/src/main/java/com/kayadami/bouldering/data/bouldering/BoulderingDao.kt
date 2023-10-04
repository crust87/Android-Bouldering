package com.kayadami.bouldering.data.bouldering

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kayadami.bouldering.data.bouldering.type.BoulderingEntity
import com.kayadami.bouldering.data.bouldering.type.BoulderingWithComments

@Dao
interface BoulderingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg bouldering: BoulderingEntity): List<Long>

    @Update
    suspend fun update(vararg bouldering: BoulderingEntity)

    @Delete
    suspend fun delete(bouldering: BoulderingEntity)

    @Query("DELETE FROM bouldering WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM bouldering ORDER BY id ASC")
    suspend fun getAllASC(): List<BoulderingEntity>

    @Query("SELECT * FROM bouldering ORDER BY id DESC")
    suspend fun getAllDESC(): List<BoulderingEntity>

    @Query("SELECT * FROM bouldering WHERE id = :id")
    suspend fun get(id: Long): BoulderingEntity?

    @Transaction
    @Query("SELECT * FROM bouldering WHERE id = :id")
    suspend fun getWithComments(id: Long): List<BoulderingWithComments>

}