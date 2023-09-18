package com.kayadami.bouldering.app.bindings

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.data.type.Bouldering


object EditorViewBindings {

    @BindingAdapter("bouldering")
    @JvmStatic
    fun setBouldering(view: EditorView, bouldering: Bouldering?) {
        bouldering?.let {
            view.setProblem(bouldering)
        }
    }

    @BindingAdapter("selectedHolder")
    @JvmStatic
    fun setSelectedHolder(view: EditorView, bouldering: HolderBox?) {
        // DO NOTHING
    }

    @InverseBindingAdapter(attribute = "selectedHolder", event = "selectedHolderChanged")
    @JvmStatic
    fun getSelectedHolder(view: EditorView): HolderBox? {
        return view.selectedHolderBox
    }

    @BindingAdapter("selectedHolderChanged")
    @JvmStatic
    fun setSelectedHolderChangeListener(view: EditorView, listener: InverseBindingListener?) {
        view.setOnSelectedChangeListener {
            listener?.onChange()
        }
    }

    @BindingAdapter("isLoading")
    @JvmStatic
    fun setLoading(view: EditorView, isLoading: Boolean?) {
        // DO NOTHING
    }

    @InverseBindingAdapter(attribute = "isLoading", event = "isLoadingChange")
    @JvmStatic
    fun isLoading(view: EditorView): Boolean {
        return view.isLoading
    }

    @BindingAdapter("isLoadingChange")
    @JvmStatic
    fun setOnProblemListener(view: EditorView, listener: InverseBindingListener?) {
        view.setOnProblemListener(object: EditorView.OnProblemListener{
            override fun onLoadingStart() {
                listener?.onChange()
            }

            override fun onLoadingFinish() {
                listener?.onChange()
            }
        })
    }
}