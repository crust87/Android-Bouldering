package com.kayadami.bouldering.app.domain

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
class SaveImageUseCase @Inject constructor(
    val contentResolver: ContentResolver,
    val imageGenerator: ImageGenerator,
) {

    suspend operator fun invoke(bouldering: Bouldering, extraName: String = ""): Uri {
        return withContext(Dispatchers.IO) {
            val bitmap = imageGenerator.createImage(bouldering)

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "bouldering_${extraName}${System.currentTimeMillis()}.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }

            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IOException("Failed to create new MediaStore record.")

            contentResolver.openOutputStream(uri)?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)

                it.close()
            } ?: throw IOException("Failed to open Output Stream.")

            uri
        }
    }
}