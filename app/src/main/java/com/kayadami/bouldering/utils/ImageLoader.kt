package com.kayadami.bouldering.utils

import android.widget.ImageView
import com.bumptech.glide.signature.ObjectKey
import com.kayadami.bouldering.app.GlideApp
import com.kayadami.bouldering.editor.data.Bouldering
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

class ImageLoader(private val transformation: RoundedCornersTransformation) {

    fun load(imageView: ImageView, bouldering: Bouldering) : Int {
        GlideApp.with(imageView)
                .load(File(bouldering.thumb))
                .fitCenter()
                .transform(transformation)
                .signature(ObjectKey(bouldering.lastModify.toString()))
                .into(imageView)

        return 0
    }
}