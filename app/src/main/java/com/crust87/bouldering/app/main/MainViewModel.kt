package com.crust87.bouldering.app.main

import androidx.lifecycle.ViewModel
import com.crust87.bouldering.app.main.type.MainItemUIState
import com.crust87.bouldering.app.main.type.MainUIState
import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.bouldering.ListSort
import com.crust87.bouldering.data.getItemType
import com.crust87.util.asDateText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val boulderingRepository: BoulderingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUIState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mainListUiItems: Flow<List<MainItemUIState>> = _uiState.flatMapLatest {
        boulderingRepository.list(it.sort).map { list ->
            if (list.isNotEmpty()) {
                list.map { bouldering ->
                    MainItemUIState(
                        bouldering.id,
                        bouldering.thumb,
                        bouldering.isSolved,
                        bouldering.createdAt.asDateText(),
                        bouldering.updatedAt,
                        bouldering.getItemType()
                    ) {
                        _eventChannel.tryEmit(OpenViewerEvent(bouldering.id))
                    }
                }
            } else {
                listOf(MainItemUIState())
            }
        }
    }

    private val _eventChannel = MutableSharedFlow<MainViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<MainViewModelEvent> = _eventChannel

    fun setSort(sort: ListSort) {
        _uiState.update {
            it.copy(sort = sort)
        }
    }
}