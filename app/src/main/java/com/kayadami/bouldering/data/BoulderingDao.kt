package com.kayadami.bouldering.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kayadami.bouldering.data.type.Bouldering

@Dao
interface BoulderingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg bouldering: Bouldering)

    @Update
    fun update(vararg bouldering: Bouldering)

    @Delete
    fun delete(bouldering: Bouldering)

    @Query("SELECT * FROM bouldering")
    fun getAll(): List<Bouldering>

    @Query("SELECT * FROM bouldering WHERE id = :id")
    fun get(id: Int): Bouldering?

}