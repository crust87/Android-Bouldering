package com.kayadami.bouldering.data.type

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kayadami.bouldering.utils.DateUtils
import java.util.*

@Entity(tableName = "bouldering")
class Bouldering(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "path")
    var path: String,
    @ColumnInfo(name = "thumb")
    var thumb: String,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "isSolved")
    var isSolved: Boolean,
    @ColumnInfo(name = "color")
    var color: String?,
    @ColumnInfo(name = "createdAt")
    var createdAt: Long,
    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long,
    @ColumnInfo(name = "holderList")
    var holderList: List<Holder>,
    @ColumnInfo(name = "orientation")
    var orientation: Int
) : Comparable<Bouldering> {

    val viewType: Int
        get() = orientation

    fun getDate(): String {
        return DateUtils.convertDate(updatedAt)
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Bouldering) {
            return id == other.id
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun compareTo(other: Bouldering): Int {
        return when {
            (other.updatedAt - updatedAt) > 0 -> 1
            (other.updatedAt - updatedAt) < 0 -> -1
            else -> 0
        }
    }
}
