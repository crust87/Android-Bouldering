package com.kayadami.bouldering.app.editor

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.data.Bouldering
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditorViewModel(
        private val repository: BoulderingDataSource,
        private val resourceManager: EditorResourceManager
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.message?.let {
            _errorEvent.value = Event(it)
        }
    }

    var bouldering = MutableLiveData<Bouldering>()
    val selectedHolder = MutableLiveData<HolderBox?>()
    val isProgress = ObservableBoolean(false)

    val title: LiveData<String> = Transformations.map(bouldering) {
        when (it.createdDate > 0) {
            true -> resourceManager.editText
            false -> resourceManager.createText
        }
    }

    val problemToolVisibility: LiveData<Int> = Transformations.map(selectedHolder) {
        if (it == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
    val holderToolVisibility: LiveData<Int> = Transformations.map(selectedHolder) {
        if (it != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val isNumberHolder: LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isInOrder ?: false
    }
    val isSpecialHolder: LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isSpecial ?: false
    }
    val isNumberEnabled: LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isNotSpecial ?: true
    }

    private val _finishEditEvent = MutableLiveData<Event<Unit>>()
    val finishEditEvent: LiveData<Event<Unit>>
        get() = _finishEditEvent

    private val _openColorChooserEvent = MutableLiveData<Event<Unit>>()
    val openColorChooserEvent: LiveData<Event<Unit>>
        get() = _openColorChooserEvent

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>>
        get() = _errorEvent

    fun setOrder(isChecked: Boolean) {
        selectedHolder.value?.isInOrder = isChecked

        if (selectedHolder.value?.isInOrder == true) {
            selectedHolder.value?.index = kotlin.Int.MAX_VALUE
        }
    }

    fun setSpecial(isChecked: Boolean) {
        selectedHolder.value?.isSpecial = isChecked
    }

    fun load(path: String, id: Long) = viewModelScope.launch(exceptionHandler) {
        bouldering.value = withContext(Dispatchers.IO) {
            when {
                id > 0 -> repository[id]
                path.isNotEmpty() -> Bouldering(path, path, null, -1, -1, ArrayList(), 0)
                else -> throw Exception()
            }
        }
    }

    fun done(editorView: EditorView) = viewModelScope.launch(exceptionHandler) {
        isProgress.set(true)

        withContext(Dispatchers.IO) {
            if (bouldering.value?.createdDate ?: -1 > 0) {
                editorView.modify().let {
                    repository.restore()
                }
            } else {
                editorView.create().let {
                    repository.add(it)
                }
            }
        }

        isProgress.set(false)

        _finishEditEvent.value = Event(Unit)
    }

    fun openColorChooser() {
        _openColorChooserEvent.value = Event(Unit)
    }
}