package com.kayadami.bouldering.data

import android.content.Context
import com.kayadami.bouldering.data.type.Bouldering
import com.kayadami.bouldering.utils.FileUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDataInitializer @Inject constructor(@ApplicationContext val context: Context) {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter = moshi.adapter(Bouldering::class.java)

    fun getMockList(): ArrayList<Bouldering> {
        val boulderingList = ArrayList<Bouldering>()

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