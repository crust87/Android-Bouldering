package com.kayadami.bouldering.app.viewer

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val contentResolver: ContentResolver,
    val imageGenerator: ImageGenerator,
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

    val navigateUpEvent = SingleLiveEvent<Unit>()

    val openKeyboardEvent = SingleLiveEvent<EditText>()

    val hideKeyboardEvent = SingleLiveEvent<Unit>()

    fun init(id: Long) {
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

        navigateUpEvent.value = Unit
    }

    fun openShare() = viewModelScope.launch(Dispatchers.Main) {
        isProgress.value = true

        try {
            val uri = saveImageInternal("share_")

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/*"

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_SUBJECT, "")
            intent.putExtra(Intent.EXTRA_TEXT, "")
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            openShareEvent.value = Intent.createChooser(intent, "Share Image")
        } catch (e: Exception) {
            e.printStackTrace()

            toastEvent.value = e.message
        }

        isProgress.value = false
    }

    fun saveImage() = viewModelScope.launch(Dispatchers.Main) {
        isProgress.value = true

        try {
            val uri = saveImageInternal()

            finishSaveEvent.value = uri.path
        } catch (e: Exception) {
            e.printStackTrace()

            toastEvent.value = e.message
        }

        isProgress.value = false
    }

    fun toggleInfoVisible(view: EditText) {
        if (view.isFocusableInTouchMode) {
            hideKeyboardEvent.value = Unit
            view.isFocusable = false
            setTitle(view.text.toString())
        } else {
            infoVisibility.value = when (infoVisibility.value) {
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    fun editTitle(view: EditText) {
        if (!view.isFocusableInTouchMode) {
            view.isFocusableInTouchMode = true
            if (view.requestFocus()) {
                openKeyboardEvent.value = view
            }
        }
    }

    private suspend fun saveImageInternal(extraName: String = ""): Uri {
        return withContext(Dispatchers.IO) {
            val bitmap = imageGenerator.createImage(bouldering.value!!)

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