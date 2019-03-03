package com.kayadami.bouldering.data

import android.content.Context
import com.google.gson.Gson
import com.kayadami.bouldering.app.setting.opensourcelicense.OpenSourceLicense
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class BoulderingTestRepository(context: Context) : BoulderingDataSource {

    private val boulderingList = ArrayList<Bouldering>()
    private val gson: Gson = Gson()

    init {
        context.assets.list("mock")?.all {
            try {
                val problem = gson.fromJson(context.assets.open("mock/$it/bouldering.json").readTextAndClose(), Bouldering::class.java)
                val problemDir = File(context.filesDir, problem.createdDate.toString())

                if (!problemDir.exists()) {
                    problemDir.mkdir()
                }

                val imageDest = File(problemDir, "image.jpg")
                val thumbDest = File(problemDir, "thumb.jpg")

                FileUtil.copy(context.assets.open("mock/$it/image.jpg"), FileOutputStream(imageDest))
                FileUtil.copy(context.assets.open("mock/$it/thumb.jpg"), FileOutputStream(thumbDest))

                problem.path = imageDest.absolutePath
                problem.thumb = thumbDest.absolutePath

                boulderingList.add(problem)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            true
        }
    }

    override fun get(): ArrayList<Bouldering> {
        return boulderingList
    }

    override operator fun get(createDate: Long): Bouldering? {
        return boulderingList.find { it.createdDate == createDate }
    }

    override fun add(bouldering: Bouldering) {
        boulderingList.add(0, bouldering)
    }

    override fun remove(bouldering: Bouldering) {
        boulderingList.remove(bouldering)
    }

    override fun restore() {
    }

    override fun exportAll(): Boolean {
        return true
    }

    override fun importAll(): Boolean {
        return true
    }

    fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }

    override fun getOpenSourceList(): List<OpenSourceLicense> {
        return ArrayList<OpenSourceLicense>().apply {
            add(OpenSourceLicense("Android Architecture Blueprints",
                    "https://github.com/googlesamples/android-architecture",
                    "Copyright 2019 Google Inc.\nApache License, Version 2.0"))

            add(OpenSourceLicense("PhotoView",
                    "https://github.com/chrisbanes/PhotoView",
                    "Copyright 2011, 2012 Chris Banes.\nApache License, Version 2.0"))

            add(OpenSourceLicense("Color Picker",
                    "https://github.com/QuadFlask/colorpicker",
                    "Copyright 2014-2017 QuadFlask.\nApache License, Version 2.0"))
        }
    }
}