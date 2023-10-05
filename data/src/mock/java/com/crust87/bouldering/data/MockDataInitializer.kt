package com.crust87.bouldering.data

import android.content.Context
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.kayadami.util.FileUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class MockDataInitializer(val context: Context) {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter = moshi.adapter(BoulderingEntity::class.java)

    fun getMockList(): ArrayList<BoulderingEntity> {
        val boulderingList = ArrayList<BoulderingEntity>()

        context.assets.list("mock")?.forEach {
            try {
                adapter.fromJson(
                    context.assets.open("mock/$it/bouldering.json").readTextAndClose(),
                )?.let { problem ->
                    val problemDir = File(context.filesDir, problem.createdAt.toString())

                    if (!problemDir.exists()) {
                        problemDir.mkdir()
                    }

                    val imageDest = File(problemDir, "image.jpg")
                    val thumbDest = File(problemDir, "thumb.jpg")

                    FileUtil.copy(
                        context.assets.open("mock/$it/image.jpg"),
                        FileOutputStream(imageDest)
                    )
                    FileUtil.copy(
                        context.assets.open("mock/$it/thumb.jpg"),
                        FileOutputStream(thumbDest)
                    )

                    problem.path = imageDest.absolutePath
                    problem.thumb = thumbDest.absolutePath

                    boulderingList.add(problem)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return boulderingList
    }

    private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }
}