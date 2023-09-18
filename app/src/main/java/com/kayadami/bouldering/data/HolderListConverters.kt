package com.kayadami.bouldering.data

import androidx.room.TypeConverter
import com.kayadami.bouldering.data.type.Holder
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HolderListConverters {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter = moshi.adapter<List<Holder>>(Types.newParameterizedType(
        MutableList::class.java,
        Holder::class.java
    ))


    @TypeConverter
    fun fromJson(value: String?): List<Holder>? {
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toJson(holderList: List<Holder>?): String? {
        return adapter.toJson(holderList)
    }
}