package com.kayadami.bouldering.app.viewer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.lifecycle.*
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerateException
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.FileUtil
import com.kayadami.bouldering.utils.PermissionChecker2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
        val repository: BoulderingDataSource,
        val imageGenerator: ImageGenerator,
        val permissionChecker: PermissionChecker2
) : ViewModel() {

    val bouldering = MutableLiveData<Bouldering>()

    val title: LiveData<String> = bouldering.map {
        when (it.title.isNullOrEmpty()) {
            true -> "NO TITLE"
            else -> it.title ?: ""
        }
    }

    val lastModify: LiveData<String> = bouldering.map {
        DateUtils.convertDateTime(it.lastModify)
    }

    val isSolved: LiveData<Boolean> = bouldering.map {
        it.isSolved
    }

    val isProgress = MutableLiveData(false)

    val infoVisibility = MutableLiveData(View.VISIBLE)

    val openEditorEvent = SingleLiveEvent<Long>()

    val toastEvent = SingleLiveEvent<String>()

    val openShareEvent = SingleLiveEvent<Intent>()

    val finishSaveEvent = SingleLiveEvent<String>()

    val requestSavePermissionEvent = SingleLiveEvent<Unit>()

    val navigateUpEvent = SingleLiveEvent<Unit>()

    fun start(id: Long) {
        bouldering.value = repository[id]
    }

    fun openEditor() {
        bouldering.value?.let {
            openEditorEvent.value = it.createdDate
        }
    }

    fun toggleSolved() {
        bouldering.value?.isSolved = !(isSolved.value == true)
        bouldering.value = bouldering.value

        repository.restore()
    }

    fun setTitle(newTitle: String?) {
        bouldering.value?.title = newTitle
        bouldering.value = bouldering.value

        repository.restore()
    }

    fun remove() {
        bouldering.value?.let {
            repository.remove(it)
        }

        navigateUpEvent.call()
    }

    fun openShare() = viewModelScope.launch(Dispatchers.Main) {
        isProgress.value = true

        try {
            val uri = withContext(Dispatchers.Default) {
                val bitmap = imageGenerator.createImage(bouldering.value!!)

                val outFile = File(FileUtil.applicationDirectory, "share_" + System.currentTimeMillis() + ".jpg")

                if (!outFile.createNewFile()) {
                    throw ImageGenerateException("Fail to Create File!")
                }

                val outStream = FileOutputStream(outFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                outStream.flush()
                outStream.close()

                Uri.parse(outFile.absolutePath)
            }

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            openShareEvent.value = Intent.createChooser(intent, "Share Image")
        } catch (e: Exception) {
            // TODO Record Exception

            toastEvent.value = e.message
        }

        isProgress.value = false
    }

    fun saveImage() = viewModelScope.launch(Dispatchers.Main) {
        if (permissionChecker.checkWrite()) {
            isProgress.value = true

            try {
                val path = withContext(Dispatchers.IO) {
                    val bitmap = imageGenerator.createImage(bouldering.value!!)

                    val dir = File(Environment.getExternalStorageDirectory(), "Bouldering")
                    if (!dir.exists()) {
                        dir.mkdir()
                    }

                    val outFile = File(dir, "bouldering_" + System.currentTimeMillis() + ".jpg")

                    if (!outFile.createNewFile()) {
                        throw ImageGenerateException("Fail to Create File!")
                    }

                    val outStream = FileOutputStream(outFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                    outStream.flush()
                    outStream.close()

                    outFile.absolutePath
                }

                finishSaveEvent.value = path
            } catch (e: Exception) {
                // TODO Record Exception

                toastEvent.value = e.message
            }

            isProgress.value = false
        } else {
            requestSavePermissionEvent.call()
        }
    }

    fun toggleInfoVisibility() {
        infoVisibility.value = when (infoVisibility.value) {
            View.VISIBLE -> View.GONE
            else -> View.VISIBLE
        }
    }
}