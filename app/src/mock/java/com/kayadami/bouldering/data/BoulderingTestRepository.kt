package com.kayadami.bouldering.data

import android.content.Context
import com.kayadami.bouldering.data.type.Bouldering
import com.kayadami.bouldering.utils.FileUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoulderingTestRepository @Inject constructor(
    @ApplicationContext context: Context
) : BoulderingDataSource {

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val adapter = moshi.adapter(Bouldering::class.java)

    private val boulderingList = ArrayList<Bouldering>()

    init {
        context.assets.list("mock")?.all {
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

            true
        }
    }

    override fun list() = flow {
        emit(boulderingList)
    }

    override suspend fun get(id: Int): Bouldering? {
        return boulderingList.find { it.id == id }
    }

    override suspend fun add(bouldering: Bouldering) {
        boulderingList.add(0, bouldering)
    }

    override suspend fun update(bouldering: Bouldering) {
        val index = boulderingList.indexOf(bouldering)
        boulderingList[index] = bouldering
    }

    override suspend fun remove(bouldering: Bouldering) {
        boulderingList.remove(bouldering)
    }

    fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }
}