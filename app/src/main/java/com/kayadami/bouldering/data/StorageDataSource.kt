package com.kayadami.bouldering.data

import android.content.Context
import com.google.gson.Gson
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.FileUtil
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

class StorageDataSource(private val context: Context) {

    private val gson: Gson = Gson()

    fun exportAll(boulderingList: ArrayList<Bouldering>) {
        val appDir = FileUtil.applicationDirectory

        appDir ?: throw IOException("applicationDirectory is not exist")

        for (p in boulderingList) {
            try {
                val problemJson = gson.toJson(p)

                val problemDir = File(appDir, p.createdDate.toString())

                if (!problemDir.exists()) {
                    problemDir.mkdir()
                }

                val problemFile = File(problemDir, "bouldering.json")
                val writer = FileWriter(problemFile)
                writer.append(problemJson)
                writer.flush()
                writer.close()

                val imageSource = File(p.path)
                val thumbSource = File(p.thumb)

                val imageDest = File(problemDir, "image.jpg")
                val thumbDest = File(problemDir, "thumb.jpg")

                FileUtil.copy(imageSource, imageDest)
                FileUtil.copy(thumbSource, thumbDest)
            } catch (e: IOException) {
                // TODO Record Exception

                throw e
            }
        }
    }

    fun importAll(): ArrayList<Bouldering> {
        val appDir = FileUtil.applicationDirectory
        val boulderingList = ArrayList<Bouldering>()

        appDir?.listFiles() ?: throw IOException("applicationDirectory is not exist")

        for (f in appDir.listFiles()) {
            if (!f.isDirectory) {
                continue
            }
            
            try {
                val problemSource = File(f, "bouldering.json")
                val imageSource = File(f, "image.jpg")
                val thumbSource = File(f, "thumb.jpg")

                val problem = gson.fromJson(FileUtil.read(problemSource), Bouldering::class.java)
                val problemDir = File(context.filesDir, problem.createdDate.toString())

                if (!problemDir.exists()) {
                    problemDir.mkdir()
                }

                val imageDest = File(problemDir, "image.jpg")
                val thumbDest = File(problemDir, "thumb.jpg")

                FileUtil.copy(imageSource, imageDest)
                FileUtil.copy(thumbSource, thumbDest)

                problem.path = imageDest.absolutePath
                problem.thumb = thumbDest.absolutePath

                boulderingList.add(problem)
            } catch (e: IOException) {
                // TODO Record Exception

                throw e
            }
        }

        return boulderingList
    }
}
