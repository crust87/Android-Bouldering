package com.kayadami.bouldering.app.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.app.domain.OpenCameraUseCase
import com.kayadami.bouldering.app.domain.OpenGalleryUseCase
import com.kayadami.bouldering.app.main.type.BoulderingItemUiState
import com.kayadami.bouldering.app.main.type.EmptyItemUiState
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val openCameraUseCase: OpenCameraUseCase,
    val openGalleryUseCae: OpenGalleryUseCase,
) : ViewModel() {

    val listSort = MutableLiveData(BoulderingDataSource.ListSort.DESC)

    val boulderingListUiItems = listSort.switchMap {
        repository.list(it).map { list ->
            if (list.isNotEmpty()) {
                list.map { bouldering ->
                    BoulderingItemUiState(bouldering, onClick = {
                        openViewer(bouldering.id)
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

    var photoPath: String?
        get() = openCameraUseCase.photoPath
        set(value) {
            openCameraUseCase.photoPath = value
        }

    fun setSort(sort: BoulderingDataSource.ListSort) {
        listSort.value = sort

        _eventChannel.tryEmit(ListSortChangeEvent)
    }

    fun openSetting() {
        _eventChannel.tryEmit(OpenSettingEvent)
    }

    fun openViewer(id: Long) {
        _eventChannel.tryEmit(OpenViewerEvent(id))
    }

    fun openEditor(path: String) {
        _eventChannel.tryEmit(OpenEditorEvent(path))
    }

    fun openCamera() {
        _eventChannel.tryEmit(OpenCameraEvent(openCameraUseCase()))
    }

    fun openGallery() {
        _eventChannel.tryEmit(OpenGalleryEvent(openGalleryUseCae()))
    }
}