package com.crust87.bouldering.app.main.domain

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.app.IODispatcher
import com.crust87.bouldering.data.asEditorBouldering
import com.crust87.bouldering.editor.ImageGenerator
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
class SaveImageUseCase @Inject constructor(
    val contentResolver: ContentResolver,
    val imageGenerator: ImageGenerator,
    @IODispatcher val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(bouldering: BoulderingEntity, extraName: String = ""): Uri {
        return withContext(ioDispatcher) {
            val bitmap = imageGenerator.createImage(bouldering.asEditorBouldering())

            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    "bouldering_${extraName}${System.currentTimeMillis()}.jpg"
                )
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