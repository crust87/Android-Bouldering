package com.kayadami.bouldering.app.domain

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.kayadami.bouldering.utils.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OpenCameraUseCase @Inject constructor(@ApplicationContext val context: Context) {

    var photoPath: String? = null

    operator fun invoke(): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile = FileUtil.createImageFile(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)) ?: throw RuntimeException("Feature Not Supported")
        val photoURI = FileProvider.getUriForFile(
            context,
            "com.kayadami.bouldering.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        photoPath = photoFile.absolutePath

        return intent
    }
}