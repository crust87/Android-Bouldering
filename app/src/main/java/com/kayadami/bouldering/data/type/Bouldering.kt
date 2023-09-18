package com.kayadami.bouldering.data.type

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kayadami.bouldering.utils.DateUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
@Entity(tableName = "bouldering")
class Bouldering(
    @Json(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Json(name = "path")
    @ColumnInfo(name = "path")
    var path: String,
    @Json(name = "thumb")
    @ColumnInfo(name = "thumb")
    var thumb: String,
    @Json(name = "title")
    @ColumnInfo(name = "title")
    var title: String?,
    @Json(name = "isSolved")
    @ColumnInfo(name = "isSolved")
    var isSolved: Boolean,
    @Json(name = "color")
    @ColumnInfo(name = "color")
    var color: String?,
    @Json(name = "createdAt")
    @ColumnInfo(name = "createdAt")
    var createdAt: Long,
    @Json(name = "updatedAt")
    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long,
    @Json(name = "holderList")
    @ColumnInfo(name = "holderList")
    var holderList: List<Holder>,
    @Json(name = "orientation")
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
