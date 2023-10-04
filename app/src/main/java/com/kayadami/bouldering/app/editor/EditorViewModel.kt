package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.*
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.IODispatcher
import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val resources: Resources,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
    @IODispatcher val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val bouldering = MutableLiveData<Bouldering>()

    val title: LiveData<String> = bouldering.map {
        when (it.id > 0) {
            true -> resources.getString(R.string.editor_edit)
            false -> resources.getString(R.string.editor_create)
        }
    }

    val selectedHolder = MutableLiveData<HolderBox?>()

    val problemToolVisibility: LiveData<Int> = selectedHolder.map {
        if (it == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val holderToolVisibility: LiveData<Int> = selectedHolder.map {
        if (it != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val isNumberHolder: LiveData<Boolean> = selectedHolder.map {
        it?.isInOrder ?: false
    }

    val isSpecialHolder: LiveData<Boolean> = selectedHolder.map {
        it?.isSpecial ?: false
    }

    val isNumberEnabled: LiveData<Boolean> = selectedHolder.map {
        it?.isNotSpecial ?: true
    }

    val isProgress = MutableLiveData(false)

    private val _eventChannel = MutableSharedFlow<EditorViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<EditorViewModelEvent> = _eventChannel

    fun init(path: String, id: Long) = viewModelScope.launch(mainDispatcher) {
        try {
            bouldering.value = when {
                id > 0 -> repository.get(id)
                path.isNotEmpty() -> Bouldering(0, path, path, null, false,null, -1, -1, ArrayList(), 0)
                else -> null
            } ?: throw Exception("NO BOULDERING HAS BEEN FOUND")
        } catch (e: Exception) {
            _eventChannel.tryEmit(ToastEvent(e.message))
        }
    }

    fun done(editorView: EditorView) = viewModelScope.launch(mainDispatcher) {
        isProgress.value = true

        try {
            if ((bouldering.value?.id ?: 0) > 0) {
                withContext(ioDispatcher) {
                    editorView.modify()
                }.let {
                    repository.update(it)
                }
            } else {
                withContext(ioDispatcher) {
                    editorView.create()
                }.let {
                    repository.add(it)
                }
            }

            _eventChannel.tryEmit(NavigateUpEvent)
        } catch (e: Exception) {
            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        isProgress.value = false
    }

    fun openColorPicker() {
        _eventChannel.tryEmit(OpenColorPickerEvent)
    }
}