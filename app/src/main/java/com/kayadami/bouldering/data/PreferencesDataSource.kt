package com.kayadami.bouldering.data

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kayadami.bouldering.utils.SharedPrefsHelper
import com.kayadami.bouldering.editor.data.Bouldering
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesDataSource @Inject constructor(@ApplicationContext val context: Context) {

    companion object {
        const val KEY_PREFERENCES_NAME = "bouldering"
        const val KEY_DATA_LIST = "dataList"
    }

    private val gson: Gson = Gson()
    private var boulderingList: ArrayList<Bouldering>
    private val sharedPrefsHelper: SharedPrefsHelper =
        SharedPrefsHelper(context.getSharedPreferences(KEY_PREFERENCES_NAME, Context.MODE_PRIVATE))

    init {
        val listString = sharedPrefsHelper[KEY_DATA_LIST, "[]"]

        if (!TextUtils.isEmpty(listString)) {
            boulderingList = gson.fromJson<ArrayList<Bouldering>>(
                listString,
                object : TypeToken<ArrayList<Bouldering>>() {}.type
            ) ?: ArrayList()
        } else {
            boulderingList = ArrayList()
        }

        boulderingList.sort()
    }

    fun get(): ArrayList<Bouldering> {
        return boulderingList
    }

    operator fun get(createDate: Long): Bouldering? {
        return boulderingList.find { it.createdDate == createDate }
    }

    fun add(bouldering: Bouldering) {
        if (!boulderingList.contains(bouldering)) {
            boulderingList.add(0, bouldering)
            restore()
        }
    }

    fun remove(bouldering: Bouldering) {
        val thumb = File(bouldering.thumb)
        thumb.delete()
        val image = File(bouldering.path)
        image.delete()
        val dir = File(context.filesDir, bouldering.createdDate.toString())
        dir.delete()
        boulderingList.remove(bouldering)

        restore()
    }

    fun restore() {
        sharedPrefsHelper.put(KEY_DATA_LIST, gson.toJson(boulderingList))

        boulderingList.sort()
    }
}
