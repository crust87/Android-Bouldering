package com.kayadami.bouldering.app.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.data.Bouldering

class MainViewModel(context: Application, val repository: BoulderingDataSource) : AndroidViewModel(context) {

    var photoPath: String? = null

    val openBoulderingEvent = MutableLiveData<Event<Int>>()
    val openCameraEvent = MutableLiveData<Event<Unit>>()
    val openGalleryEvent = MutableLiveData<Event<Unit>>()

    operator fun get(index: Int): Bouldering {
        return repository.get()[index]
    }

    fun isNotEmpty(): Boolean {
        return repository.get().isNotEmpty()
    }

    val size: Int
        get() = repository.get().size

    fun openCamera() {
        openCameraEvent.value = Event(Unit)
    }

    fun openGallery() {
        openGalleryEvent.value = Event(Unit)
    }
}