package com.kayadami.bouldering.app.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.type.Bouldering
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: BoulderingDataSource
) : ViewModel() {

    val list: LiveData<List<Bouldering>> by lazy {
        repository.list().asLiveData(viewModelScope.coroutineContext)
    }

    val eventChannel = MutableSharedFlow<MainViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1,
    )

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
        eventChannel.tryEmit(OpenCameraEvent)
    }

    fun openGallery() {
        eventChannel.tryEmit(OpenGalleryEvent)
    }
}