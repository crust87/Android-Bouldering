package com.kayadami.bouldering.app.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kayadami.bouldering.editor.EditorView
import com.kayadami.bouldering.editor.data.Bouldering

object BoulderingViewBindings {

    @BindingAdapter("loadImage")
    @JvmStatic
    fun loadImage(imageView: ImageView, result: Int) {
    }
}
