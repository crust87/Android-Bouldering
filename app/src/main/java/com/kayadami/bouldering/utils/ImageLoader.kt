package com.kayadami.bouldering.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.signature.ObjectKey
import com.kayadami.bouldering.app.GlideApp
import com.kayadami.bouldering.editor.data.Bouldering
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

abstract class ImageLoader(private val transformation: RoundedCornersTransformation) {

    fun load(imageView: ImageView, bouldering: Bouldering): Int {
        GlideApp.with(imageView)
                .load(File(bouldering.thumb))
                .fitCenter()
                .transform(transformation)
                .signature(ObjectKey(bouldering.lastModify.toString()))
                .into(imageView)

        return 0
    }

    abstract fun start()

    class ActivityImageLoader(
            val activity: Activity,
            val transformation: RoundedCornersTransformation
    ) : ImageLoader(transformation) {
        override fun start() {
            Log.d("WTF", "ActivityImageLoader start")
        }
    }

    class FragmentImageLoader(
            val fragment: Fragment,
            val transformation: RoundedCornersTransformation
    ) : ImageLoader(transformation) {
        override fun start() {
            Log.d("WTF", "FragmentImageLoader start")
        }
    }

    class ApplicationImageLoader(
            val context: Context,
            val transformation: RoundedCornersTransformation
    ) : ImageLoader(transformation) {
        override fun start() {
            Log.d("WTF", "ApplicationImageLoader start")
        }
    }

    object Builder {
        fun create(
                activity: Activity,
                transformation: RoundedCornersTransformation
        ): ImageLoader {
            return ActivityImageLoader(activity, transformation)
        }

        fun create(
                fragment: Fragment,
                transformation: RoundedCornersTransformation
        ): ImageLoader {
            return FragmentImageLoader(fragment, transformation)
        }

        fun create(
                context: Context,
                transformation: RoundedCornersTransformation
        ): ImageLoader {
            return ApplicationImageLoader(context, transformation)
        }
    }
}