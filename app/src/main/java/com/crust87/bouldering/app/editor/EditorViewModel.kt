package com.crust87.bouldering.app.editor

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.app.IODispatcher
import com.crust87.bouldering.app.editor.type.EditorUIState
import com.crust87.bouldering.data.asBoulderingEntity
import com.crust87.bouldering.editor.EditorView
import com.crust87.bouldering.editor.data.Holder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val boulderingRepository: BoulderingRepository,
    @IODispatcher val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUIState())
    val uiState = _uiState.asLiveData(viewModelScope.coroutineContext)

    fun init(path: String, id: Long) = viewModelScope.launch {
        try {
            when {
                id > 0 -> boulderingRepository.get(id)?.also { data ->
                    _uiState.update {
                        it.copy(
                            id = id,
                            data = data,
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
                        )
                    }
                }
                else -> null
            } ?: throw Exception("NO BOULDERING HAS BEEN FOUND")
        } catch (e: Exception) {
            _uiState.update {
                it.copy(message = e.message)
            }
        }
    }

    fun done(editorView: EditorView) = viewModelScope.launch {
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

            _uiState.update {
                it.copy(isEditDone = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            _uiState.update {
                it.copy(message = e.message)
            }
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

    fun setHolder(holder: Holder?) {
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

    fun consumeMessage() {
        _uiState.update {
            it.copy(message = null)
        }
    }
}