package com.kayadami.bouldering.app.viewer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerateException
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.FileUtil
import com.kayadami.bouldering.utils.PermissionChecker2
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ViewerViewModel @ViewModelInject constructor(
        val repository: BoulderingDataSource,
        val imageGenerator: ImageGenerator,
        val permissionChecker: PermissionChecker2
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        FirebaseCrashlytics.getInstance().recordException(throwable)
        throwable.printStackTrace()

        throwable.message?.let {
            toastEvent.value = it
        }
    }

    val isProgress = ObservableBoolean(false)
    val isShow = ObservableBoolean(true)

    val bouldering = MutableLiveData<Bouldering>()

    val title: LiveData<String> = Transformations.map(bouldering) {
        when (it.title.isNullOrEmpty()) {
            true -> "NO TITLE"
            else -> it.title
        }
    }

    val lastModify: LiveData<String> = Transformations.map(bouldering) {
        DateUtils.convertDateTime(it.lastModify)
    }

    val isSolved = ObservableBoolean()

    val openEditorEvent = SingleLiveEvent<Long>()
    val toastEvent = SingleLiveEvent<String>()
    val openShareEvent = SingleLiveEvent<Intent>()
    val finishSaveEvent = SingleLiveEvent<String>()
    val requestSavePermissionEvent = SingleLiveEvent<Unit>()

    fun start(id: Long) {
        bouldering.value = repository[id]
        isSolved.set(bouldering.value?.isSolved == true)
    }

    fun openEditor() {
        bouldering.value?.let {
            openEditorEvent.value = it.createdDate
        }
    }

    fun toggleSolved() {
        val value = bouldering.value?.isSolved == true

        bouldering.value?.isSolved = !value
        isSolved.set(!value)

        repository.restore()
    }

    fun setTitle(newTitle: String?) {
        bouldering.value?.let {
            it.title = newTitle

            repository.restore()
        }
    }

    fun remove() {
        repository.remove(bouldering.value!!)
    }

    fun openShare() = viewModelScope.launch(exceptionHandler) {
        isProgress.set(true)

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

        isProgress.set(false)

        openShareEvent.value = Intent.createChooser(intent, "Share Image")
    }

    fun saveImage() = viewModelScope.launch(exceptionHandler) {
        if (permissionChecker.checkWrite()) {
            isProgress.set(true)

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

            isProgress.set(false)
        } else {
            requestSavePermissionEvent.call()
        }
    }
}