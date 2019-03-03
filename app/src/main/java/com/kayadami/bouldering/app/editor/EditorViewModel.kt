package com.kayadami.bouldering.app.editor

import android.app.Application
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.kayadami.bouldering.Event
import com.kayadami.bouldering.R
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.BoulderingException
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.data.Bouldering
import java.util.ArrayList

class EditorViewModel(private val context: Application,
                      private val repository: BoulderingDataSource) : AndroidViewModel(context) {

    val isProgress = ObservableBoolean(false)

    var bouldering = MutableLiveData<Bouldering>()

    val title : LiveData<String> = Transformations.map(bouldering) {
        when (it.createdDate > 0) {
            true -> context.resources.getString(R.string.editor_edit)
            false -> context.resources.getString(R.string.editor_create)
        }
    }

    val selectedHolder = MutableLiveData<HolderBox?>().apply {
        value = null
    }
    val isBoulderToolShow : LiveData<Boolean> = Transformations.map(selectedHolder) {
        it == null
    }
    val isHolderToolShow : LiveData<Boolean> = Transformations.map(selectedHolder) {
        it != null
    }
    val isNumberHolder : LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isInOrder ?: false
    }
    val isSpecialHolder : LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isSpecial ?: false
    }
    val isNumberEnabled : LiveData<Boolean> = Transformations.map(selectedHolder) {
        it?.isNotSpecial ?: true
    }

    val finishEditEvent = MutableLiveData<Event<Unit>>()

    fun setOrder(isChecked: Boolean) {
        selectedHolder.value?.isInOrder = isChecked

        if (selectedHolder.value?.isInOrder == true) {
            selectedHolder.value?.index = kotlin.Int.MAX_VALUE
        }
    }

    fun setSpecial(isChecked: Boolean) {
        selectedHolder.value?.isSpecial = isChecked
    }

    fun load(path: String, id: Long) {
        bouldering.value = when {
            id > 0 -> repository[id]
            path.isNotEmpty() -> Bouldering(path, path, null, -1, -1, ArrayList(), 0)
            else -> throw Exception()
        }
    }

    fun store(editorView: EditorView) {
        try {
            if (bouldering.value?.createdDate ?: -1 > 0) {
                editorView.modify().let {
                    repository.restore()
                }
            } else {
                editorView.create().let {
                    repository.add(it)
                }
            }
        } catch (e: BoulderingException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}