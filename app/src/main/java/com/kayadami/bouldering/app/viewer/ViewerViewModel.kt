package com.kayadami.bouldering.app.viewer

import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.app.domain.SaveImageUseCase
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.type.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val saveImageUseCase: SaveImageUseCase,
) : ViewModel() {

    val bouldering = MutableLiveData<Bouldering>()

    val title: LiveData<String> = bouldering.map {
        when (it.title.isNullOrEmpty()) {
            true -> "NO TITLE"
            else -> it.title ?: ""
        }
    }

    val lastModify: LiveData<String> = bouldering.map {
        DateUtils.convertDateTime(it.updatedAt)
    }

    val isSolved: LiveData<Boolean> = bouldering.map {
        it.isSolved
    }

    val isProgress = MutableLiveData(false)

    val infoVisibility = MutableLiveData(View.VISIBLE)

    val openEditorEvent = SingleLiveEvent<Bouldering>()

    val toastEvent = SingleLiveEvent<String>()

    val openShareEvent = SingleLiveEvent<Intent>()

    val finishSaveEvent = SingleLiveEvent<String>()

    val navigateUpEvent = SingleLiveEvent<Unit>()

    val openKeyboardEvent = SingleLiveEvent<EditText>()

    val hideKeyboardEvent = SingleLiveEvent<Unit>()

    fun init(id: Int) = viewModelScope.launch(Dispatchers.Main) {
        bouldering.value = repository.get(id)
    }

    fun openEditor() {
        bouldering.value?.let {
            openEditorEvent.value = it
        }
    }

    fun toggleSolved() = viewModelScope.launch(Dispatchers.Main) {
        bouldering.value?.let {
            it.isSolved = !it.isSolved
            bouldering.value = it

            repository.update(it)
        }
    }

    fun setTitle(newTitle: String?) = viewModelScope.launch(Dispatchers.Main) {
        bouldering.value?.let {
            it.title = newTitle
            bouldering.value = it

            repository.update(it)
        }
    }

    fun remove() = viewModelScope.launch(Dispatchers.Main) {
        bouldering.value?.let {
            repository.remove(it)
        }

        navigateUpEvent.value = Unit
    }

    fun openShare() = viewModelScope.launch(Dispatchers.Main) {
        isProgress.value = true

        try {
            val uri = saveImageUseCase(bouldering.value!!, "share_")

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
            val uri = saveImageUseCase(bouldering.value!!)

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
}