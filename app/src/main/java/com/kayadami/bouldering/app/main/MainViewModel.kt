package com.kayadami.bouldering.app.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.data.Bouldering
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        val repository: BoulderingDataSource
) : ViewModel() {

    val list: LiveData<List<Bouldering>> by lazy {
        repository.list().asLiveData(viewModelScope.coroutineContext)
    }

    val openBoulderingEvent = SingleLiveEvent<Bouldering>()
    val openCameraEvent = SingleLiveEvent<Unit>()
    val openGalleryEvent = SingleLiveEvent<Unit>()

    fun openCamera() {
        openCameraEvent.value = Unit
    }

    fun openGallery() {
        openGalleryEvent.value = Unit
    }
}