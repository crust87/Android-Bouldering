package com.kayadami.bouldering.app.viewer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.crashlytics.android.Crashlytics
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerateException
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.FileUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ViewerViewModel(
        private val repository: BoulderingDataSource,
        private val imageGenerator: ImageGenerator
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Crashlytics.logException(throwable)
        throwable.printStackTrace()

        throwable.message?.let {
            _errorEvent.value = Event(it)
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

    private val _openEditorEvent = MutableLiveData<Event<Long>>()
    val openEditorEvent: LiveData<Event<Long>>
        get() = _openEditorEvent

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    private val _openShareEvent = MutableLiveData<Event<Intent>>()
    val openShareEvent: LiveData<Event<Intent>>
        get() = _openShareEvent

    fun start(id: Long) {
        bouldering.value = repository[id]
        isSolved.set(bouldering.value?.isSolved == true)
    }

    fun openEditor() {
        bouldering.value?.let {
            _openEditorEvent.value = Event(it.createdDate)
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

        try {
            val uri = withContext(Dispatchers.Default) { createShareImage() }

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            _openShareEvent.value = Event(Intent.createChooser(intent, "Share Image"))
        } catch (e: Throwable) {
            throw Exception("FAIL TO SHARE")
        }

        isProgress.set(false)
    }

    private fun createShareImage(): Uri {
        val bitmap = imageGenerator.createImage(bouldering.value!!)

        val outFile = File(FileUtil.applicationDirectory, "share_" + System.currentTimeMillis() + ".jpg")

        if (!outFile.createNewFile()) {
            throw ImageGenerateException("Fail to Create File!")
        }

        val outStream = FileOutputStream(outFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
        outStream.flush()
        outStream.close()

        return Uri.parse(outFile.absolutePath)
    }
}