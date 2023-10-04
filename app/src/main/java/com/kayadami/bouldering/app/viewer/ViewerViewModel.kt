package com.kayadami.bouldering.app.viewer

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.app.domain.SaveImageUseCase
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.toShareIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ViewerViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val saveImageUseCase: SaveImageUseCase,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
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

    private val _eventChannel = MutableSharedFlow<ViewerViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<ViewerViewModelEvent> = _eventChannel

    fun init(id: Long) = viewModelScope.launch(mainDispatcher) {
        bouldering.value = repository.get(id)
    }

    fun openEditor() {
        bouldering.value?.let {
            _eventChannel.tryEmit(OpenEditorEvent(it))
        }
    }

    fun remove(activity: Activity) = viewModelScope.launch(mainDispatcher) {
        val result = suspendCancellableCoroutine { cont ->
            AlertDialog.Builder(activity)
                .setMessage(R.string.alert_delete)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    cont.resume(true)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    cont.resume(false)
                }
                .setOnCancelListener {
                }
                .show()
        }

        if (result) {
            bouldering.value?.let {
                repository.remove(it)
            }

            _eventChannel.tryEmit(NavigateUpEvent)
        }
    }

    fun toggleSolved() = viewModelScope.launch(mainDispatcher) {
        bouldering.value?.let {
            it.isSolved = !it.isSolved
            bouldering.value = it

            repository.update(it)
        }
    }

    fun openComment() {
        _eventChannel.tryEmit(OpenCommentEvent)
    }

    fun openShare() = viewModelScope.launch(mainDispatcher) {
        isProgress.value = true

        try {
            val uri = saveImageUseCase(bouldering.value!!, "share_")

            val intent = uri.toShareIntent()

            _eventChannel.tryEmit(OpenShareEvent(intent))
        } catch (e: Exception) {
            e.printStackTrace()

            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        isProgress.value = false
    }

    fun saveImage() = viewModelScope.launch(mainDispatcher) {
        isProgress.value = true

        try {
            val uri = saveImageUseCase(bouldering.value!!)

            _eventChannel.tryEmit(FinishSaveEvent(uri.path))
        } catch (e: Exception) {
            e.printStackTrace()

            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        isProgress.value = false
    }

    fun toggleInfoVisible(view: EditText) {
        if (view.isFocusableInTouchMode) {
            finishEditTitle(view)
        } else {
            infoVisibility.value = when (infoVisibility.value) {
                View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    fun startEditTitle(view: EditText) {
        _eventChannel.tryEmit(OpenKeyboardEvent(view))
    }

    fun finishEditTitle(view: EditText) = viewModelScope.launch(mainDispatcher) {
        _eventChannel.tryEmit(HideKeyboardEvent(view))
        bouldering.value?.let {
            it.title = view.text.toString()
            bouldering.value = it

            repository.update(it)
        }
    }
}