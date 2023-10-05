package com.crust87.bouldering.app.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.bouldering.ListSort
import com.crust87.bouldering.app.main.type.BoulderingItemUiState
import com.crust87.bouldering.app.main.type.EmptyItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val boulderingRepository: BoulderingRepository,
) : ViewModel() {

    val listSort = MutableLiveData(ListSort.DESC)

    val boulderingListUiItems = listSort.switchMap {
        boulderingRepository.list(it).map { list ->
            if (list.isNotEmpty()) {
                list.map { bouldering ->
                    BoulderingItemUiState(bouldering, onClick = {
                        _eventChannel.tryEmit(OpenViewerEvent(bouldering.id))
                    })
                }
            } else {
                listOf(EmptyItemUiState)
            }
        }.asLiveData(viewModelScope.coroutineContext)
    }

    private val _eventChannel = MutableSharedFlow<MainViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val eventChannel: SharedFlow<MainViewModelEvent> = _eventChannel

    fun setSort(sort: ListSort) {
        listSort.value = sort

        _eventChannel.tryEmit(ListSortChangeEvent)
    }
}