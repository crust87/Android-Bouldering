package com.kayadami.bouldering.data.bouldering

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.bouldering.type.BoulderingWithComments

@Dao
interface BoulderingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg bouldering: Bouldering): List<Long>

    @Update
    fun update(vararg bouldering: Bouldering)

    @Delete
    fun delete(bouldering: Bouldering)

    @Query("DELETE FROM bouldering WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM bouldering")
    fun getAll(): List<Bouldering>

    @Query("SELECT * FROM bouldering WHERE id = :id")
    fun get(id: Long): Bouldering?

    @Transaction
    @Query("SELECT * FROM bouldering WHERE id = :id")
    fun getWithComments(id: Long): List<BoulderingWithComments>

}