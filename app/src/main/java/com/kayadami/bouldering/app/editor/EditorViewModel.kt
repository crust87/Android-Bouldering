package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import androidx.lifecycle.*
import com.kayadami.bouldering.R
import com.kayadami.bouldering.SingleLiveEvent
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.data.Bouldering
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val repository: BoulderingDataSource,
    val resources: Resources
) : ViewModel() {

    val bouldering = MutableLiveData<Bouldering>()

    val title: LiveData<String> = bouldering.map {
        when (it.createdDate > 0) {
            true -> resources.getString(R.string.editor_edit)
            false -> resources.getString(R.string.editor_create)
        }
    }

    val selectedHolder = MutableLiveData<HolderBox?>()

    val problemToolVisibility: LiveData<Int> = selectedHolder.map {
        if (it == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val holderToolVisibility: LiveData<Int> = selectedHolder.map {
        if (it != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    val isNumberHolder: LiveData<Boolean> = selectedHolder.map {
        it?.isInOrder ?: false
    }

    val isSpecialHolder: LiveData<Boolean> = selectedHolder.map {
        it?.isSpecial ?: false
    }

    val isNumberEnabled: LiveData<Boolean> = selectedHolder.map {
        it?.isNotSpecial ?: true
    }

    val isProgress = MutableLiveData(false)

    val openColorChooserEvent = SingleLiveEvent<Unit>()

    val toastEvent = SingleLiveEvent<String>()

    val navigateUpEvent = SingleLiveEvent<Unit>()

    fun init(path: String, id: Long) = viewModelScope.launch(Dispatchers.Main) {
        try {
            bouldering.value = withContext(Dispatchers.IO) {
                when {
                    id > 0 -> repository[id]
                    path.isNotEmpty() -> Bouldering(path, path, null, -1, -1, ArrayList(), 0)
                    else -> null
                }
            } ?: throw Exception("NO BOULDERING HAS BEEN FOUND")
        } catch (e: Exception) {
            toastEvent.value = e.message
        }
    }

    fun done(editorView: EditorView) = viewModelScope.launch(Dispatchers.Main) {
        isProgress.value = true

        try {
            withContext(Dispatchers.IO) {
                if ((bouldering.value?.createdDate ?: -1) > 0) {
                    editorView.modify().let {
                        repository.restore()
                    }
                } else {
                    editorView.create().let {
                        repository.add(it)
                    }
                }
            }

            navigateUpEvent.value = Unit
        } catch (e: Exception) {
            toastEvent.value = e.message
        }

        isProgress.value = false
    }

    fun openColorChooser() {
        openColorChooserEvent.value = Unit
    }
}