package com.crust87.bouldering.data.bouldering

import androidx.room.TypeConverter
import com.crust87.bouldering.data.bouldering.type.HolderData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HolderListConverters {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter = moshi.adapter<List<HolderData>>(Types.newParameterizedType(
        MutableList::class.java,
        HolderData::class.java
    ))

    @TypeConverter
    fun fromJson(value: String?): List<HolderData>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(holderList: List<HolderData>?): String? {
        return adapter.toJson(holderList)
    }
}