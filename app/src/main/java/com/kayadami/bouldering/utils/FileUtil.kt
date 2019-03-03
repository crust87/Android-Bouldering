package com.kayadami.bouldering.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    val applicationDirectory: File?
        get() {
            val path = Environment.getExternalStorageDirectory().absolutePath + "/Bouldering"

            val directory = File(path)

            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    return null
                }
            }

            return if (directory.exists()) {
                directory
            } else {
                null
            }
        }

    @Throws(IOException::class)
    fun store(image: Bitmap, dest: File) {
        if (dest.exists()) {
            dest.delete()
        }

        dest.createNewFile()

        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        val fos = FileOutputStream(dest)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File) {
        src.copyTo(dst, true, 1024)
    }

    @Throws(IOException::class)
    fun copy(src: InputStream, dst: OutputStream) {
        val buffer = ByteArray(1024)

        var read = src.read(buffer)
        while (read != -1) {
            dst.write(buffer, 0, read)
            read = src.read(buffer)
        }
    }

    @Throws(IOException::class)
    fun read(file: File): String {
        val text = StringBuilder()

        val br = BufferedReader(FileReader(file))

        for (line in br.readLine()) {
            text.append(line)
        }

        br.close()

        return text.toString()
    }

    @Throws(IOException::class)
    fun createImageFile(directory: File?): File? {
        directory ?: return null

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(imageFileName, ".jpg", directory)

        return image
    }
}