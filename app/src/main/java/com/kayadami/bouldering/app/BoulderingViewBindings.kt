package com.kayadami.bouldering.app

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.data.Bouldering

object BoulderingViewBindings {

    @BindingAdapter("bouldering")
    @JvmStatic
    fun setBouldering(editorView: EditorView, bouldering: Bouldering?) {
        bouldering?.let {
            editorView.setProblem(bouldering)
        }
    }

    @BindingAdapter("loadImage")
    @JvmStatic
    fun loadImage(imageView: ImageView, result: Int) {
    }
}
