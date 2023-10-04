package com.kayadami.bouldering.app.viewer

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.app.main.domain.SaveImageUseCase
import com.kayadami.bouldering.app.viewer.type.ViewerUIState
import com.kayadami.bouldering.data.BoulderingRepository
import com.kayadami.bouldering.utils.DateUtils
import com.kayadami.bouldering.utils.toShareIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ViewerViewModel @Inject constructor(
    val boulderingRepository: BoulderingRepository,
    val saveImageUseCase: SaveImageUseCase,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewerUIState())
    val uiState = _uiState.asLiveData(viewModelScope.coroutineContext)

    private val _eventChannel = MutableSharedFlow<ViewerViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<ViewerViewModelEvent> = _eventChannel

    fun init(id: Long) = viewModelScope.launch(mainDispatcher) {
        boulderingRepository.get(id)?.also { data ->
            _uiState.update {
                it.copy(
                    id = id,
                    data = data,
                    title = data.title.displayTitle(),
                    lastModify = DateUtils.convertDateTime(data.updatedAt),
                    isSolved = data.isSolved
                )
            }
        }
    }

    fun openEditor() {
        _eventChannel.tryEmit(OpenEditorEvent(_uiState.value.id))
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
            boulderingRepository.remove(_uiState.value.id)

            _eventChannel.tryEmit(NavigateUpEvent)
        }
    }

    fun toggleSolved() = viewModelScope.launch(mainDispatcher) {
        val isSolved = !_uiState.value.isSolved
        boulderingRepository.update(_uiState.value.id, isSolved = isSolved)
        _uiState.update {
            it.copy(isSolved = isSolved)
        }
    }

    fun openComment() {
        _eventChannel.tryEmit(OpenCommentEvent)
    }

    fun openShare() = viewModelScope.launch(mainDispatcher) {
        _uiState.update {
            it.copy(progressVisibility = View.VISIBLE)
        }

        try {
            val uri = saveImageUseCase(_uiState.value.data!!, "share_")

            val intent = uri.toShareIntent()

            _eventChannel.tryEmit(OpenShareEvent(intent))
        } catch (e: Exception) {
            e.printStackTrace()

            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        _uiState.update {
            it.copy(progressVisibility = View.GONE)
        }
    }

    fun saveImage() = viewModelScope.launch(mainDispatcher) {
        _uiState.update {
            it.copy(progressVisibility = View.VISIBLE)
        }

        try {
            val uri = saveImageUseCase(_uiState.value.data!!)

            _eventChannel.tryEmit(FinishSaveEvent(uri.path))
        } catch (e: Exception) {
            e.printStackTrace()

            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        _uiState.update {
            it.copy(progressVisibility = View.GONE)
        }
    }

    fun toggleInfoVisible(view: EditText) {
        if (view.isFocusableInTouchMode) {
            finishEditTitle(view)
        } else {
            _uiState.update {
                it.copy(
                    infoVisibility = when (it.infoVisibility) {
                        View.VISIBLE -> View.GONE
                        else -> View.VISIBLE
                    }
                )
            }
        }
    }

    fun startEditTitle(view: EditText) {
        _eventChannel.tryEmit(OpenKeyboardEvent(view))
    }

    fun finishEditTitle(view: EditText) = viewModelScope.launch(mainDispatcher) {
        _eventChannel.tryEmit(HideKeyboardEvent(view))

        val title = view.text.toString()

        boulderingRepository.update(_uiState.value.id, title = title)
        _uiState.update {
            it.copy(title = title.displayTitle())
        }
    }

    fun setLoading(toProgress: Boolean) {
        _uiState.update {
            it.copy(
                progressVisibility = when (toProgress) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            )
        }
    }

    private fun String?.displayTitle(): String {
        return when (isNullOrBlank()) {
            true -> "NO TITLE"
            else -> this
        }
    }
}