package com.kayadami.bouldering.app.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.app.domain.OpenCameraUseCase
import com.kayadami.bouldering.app.domain.OpenGalleryUseCase
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val openCameraUseCase: OpenCameraUseCase,
    val openGalleryUseCae: OpenGalleryUseCase,
) : ViewModel() {

    val listSort = MutableLiveData(BoulderingDataSource.ListSort.DESC)

    val list = listSort.switchMap {
        repository.list(it).asLiveData(viewModelScope.coroutineContext)
    }

    val eventChannel = MutableSharedFlow<MainViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    var photoPath: String?
        get() = openCameraUseCase.photoPath
        set(value) {
            openCameraUseCase.photoPath = value
        }

    fun setSort(sort: BoulderingDataSource.ListSort) {
        listSort.value = sort

        eventChannel.tryEmit(ListSortChangeEvent)
    }

    fun openSetting() {
        eventChannel.tryEmit(OpenSettingEvent)
    }

    fun openViewer(data: Bouldering) {
        eventChannel.tryEmit(OpenViewerEvent(data))
    }

    fun openEditor(path: String) {
        eventChannel.tryEmit(OpenEditorEvent(path))
    }

    fun openCamera() {
        eventChannel.tryEmit(OpenCameraEvent(openCameraUseCase()))
    }

    fun openGallery() {
        eventChannel.tryEmit(OpenGalleryEvent(openGalleryUseCae()))
    }
}