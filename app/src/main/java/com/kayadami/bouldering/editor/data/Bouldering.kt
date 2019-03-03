package com.kayadami.bouldering.editor.data

import com.google.gson.annotations.SerializedName
import com.kayadami.bouldering.utils.DateUtils
import java.util.*

open class Bouldering(@field:SerializedName("path") var path: String,
                      @field:SerializedName("thumb") var thumb: String,
                      @field:SerializedName("color") var color: String?,
                      @field:SerializedName("createdDate") var createdDate: Long,
                      @field:SerializedName("lastModify") var lastModify: Long,
                      @field:SerializedName("holderList") var holderList: ArrayList<Holder>,
                      @field:SerializedName("orientation") var orientation: Int) : Comparable<Bouldering> {

    val viewType: Int
        get() = orientation

    @SerializedName("title")
    var title: String? = null

    @SerializedName("isSolved")
    var isSolved: Boolean = false

    fun getDate(): String {
        return DateUtils.convertDate(lastModify)
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Bouldering) {
            return other.createdDate == createdDate
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun compareTo(other: Bouldering): Int {
        return when {
            (other.lastModify - lastModify) > 0 -> 1
            (other.lastModify - lastModify) < 0 -> -1
            else -> 0
        }
    }
}
