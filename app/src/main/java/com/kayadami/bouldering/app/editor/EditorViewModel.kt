package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.IODispatcher
import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.app.editor.type.EditorUIState
import com.kayadami.bouldering.data.asBoulderingEntity
import com.kayadami.bouldering.editor.EditorView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val boulderingRepository: BoulderingRepository,
    val resources: Resources,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
    @IODispatcher val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUIState())
    val uiState = _uiState.asLiveData(viewModelScope.coroutineContext)

    private val _eventChannel = MutableSharedFlow<EditorViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<EditorViewModelEvent> = _eventChannel

    fun init(path: String, id: Long) = viewModelScope.launch(mainDispatcher) {
        try {
            when {
                id > 0 -> boulderingRepository.get(id)?.also { data ->
                    _uiState.update {
                        it.copy(
                            id = id,
                            data = data,
                            title = resources.getString(R.string.editor_edit),
                        )
                    }
                }
                path.isNotEmpty() -> {
                    _uiState.update {
                        it.copy(
                            id = 0,
                            data = BoulderingEntity(
                                0,
                                path,
                                path,
                                null,
                                false,
                                null,
                                -1,
                                -1,
                                ArrayList(),
                                0
                            ),
                            title = resources.getString(R.string.editor_create),
                        )
                    }
                }
                else -> null
            } ?: throw Exception("NO BOULDERING HAS BEEN FOUND")
        } catch (e: Exception) {
            _eventChannel.tryEmit(ToastEvent(e.message))
        }
    }

    fun done(editorView: EditorView) = viewModelScope.launch(mainDispatcher) {
        _uiState.update {
            it.copy(problemToolVisibility = View.VISIBLE)
        }

        try {
            if (_uiState.value.id > 0) {
                withContext(ioDispatcher) {
                    editorView.modify()
                }.let { new ->
                    _uiState.value.data?.let { old ->
                        boulderingRepository.update(new.asBoulderingEntity(old))
                    }
                }
            } else {
                withContext(ioDispatcher) {
                    editorView.create()
                }.let {new ->
                    boulderingRepository.add(new.asBoulderingEntity(null))
                }
            }

            _eventChannel.tryEmit(NavigateUpEvent)
        } catch (e: Exception) {
            e.printStackTrace()
            _eventChannel.tryEmit(ToastEvent(e.message))
        }

        _uiState.update {
            it.copy(problemToolVisibility = View.GONE)
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

    fun openColorPicker() {
        _eventChannel.tryEmit(OpenColorPickerEvent)
    }

    fun setHolder(holder: com.kayadami.bouldering.editor.HolderBox?) {
        _uiState.update {
            it.copy(
                selected = holder,
                problemToolVisibility = if (holder == null) View.VISIBLE else View.GONE,
                holderToolVisibility = if (holder != null) View.VISIBLE else View.GONE,
                isNumberHolder = holder?.isInOrder ?: false,
                isSpecialHolder = holder?.isSpecial ?: false,
                isNumberEnabled = (holder?.isSpecial != true),
            )
        }
    }
}