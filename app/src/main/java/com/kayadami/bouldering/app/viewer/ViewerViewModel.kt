package com.kayadami.bouldering.app.viewer

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.crashlytics.android.Crashlytics
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerateException
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.FileUtil
import java.io.File
import java.io.FileOutputStream

class ViewerViewModel(var context: Application,
                      private val repository: BoulderingDataSource,
                      private val imageGenerator: ImageGenerator) : AndroidViewModel(context) {

    val isProgress = ObservableBoolean(false)
    val isShow = ObservableBoolean(true)

    val bouldering = MutableLiveData<Bouldering>()

    val title : LiveData<String> = Transformations.map(bouldering) {
        when (it.title.isNullOrEmpty()) {
            true -> "NO TITLE"
            else -> it.title
        }
    }

    val lastModify : LiveData<String> = Transformations.map(bouldering) {
        DateUtils.convertDateTime(it.lastModify)
    }

    val isSolved = ObservableBoolean()

    val openEditorEvent = MutableLiveData<Event<Long>>()

    fun start(id: Long) {
        bouldering.value = repository[id]
        isSolved.set(bouldering.value?.isSolved == true)
    }

    fun openEditor() {
        bouldering.value?.let {
            openEditorEvent.value = Event(it.createdDate)
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

    fun createShareImage(): Uri {
        try {
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
        } catch (e: Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
            throw e
        }
    }
}