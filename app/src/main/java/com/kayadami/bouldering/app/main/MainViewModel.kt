package com.kayadami.bouldering.app.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.data.Bouldering

class MainViewModel @ViewModelInject constructor(
        val repository: BoulderingDataSource
) : ViewModel() {

    var photoPath: String? = null

    val openBoulderingEvent = SingleLiveEvent<Int>()
    val openCameraEvent = SingleLiveEvent<Unit>()
    val openGalleryEvent = SingleLiveEvent<Unit>()

    operator fun get(index: Int): Bouldering {
        return repository.get()[index]
    }

    fun isNotEmpty(): Boolean {
        return repository.get().isNotEmpty()
    }

    val size: Int
        get() = repository.get().size

    fun openCamera() {
        openCameraEvent.call()
    }

    fun openGallery() {
        openGalleryEvent.call()
    }
}