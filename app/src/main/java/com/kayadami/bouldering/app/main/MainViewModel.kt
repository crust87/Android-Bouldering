package com.kayadami.bouldering.app.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.data.Bouldering

class MainViewModel @ViewModelInject constructor(
        val repository: BoulderingDataSource
) : ViewModel() {

    var photoPath: String? = null

    val list: LiveData<List<Bouldering>> by lazy {
        repository.list().asLiveData(viewModelScope.coroutineContext)
    }

    val openBoulderingEvent = SingleLiveEvent<Bouldering>()
    val openCameraEvent = SingleLiveEvent<Unit>()
    val openGalleryEvent = SingleLiveEvent<Unit>()

    fun openCamera() {
        openCameraEvent.call()
    }

    fun openGallery() {
        openGalleryEvent.call()
    }
}