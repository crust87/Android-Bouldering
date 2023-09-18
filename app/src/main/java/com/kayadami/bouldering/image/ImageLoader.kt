package com.kayadami.bouldering.image

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.signature.ObjectKey
import com.kayadami.bouldering.app.GlideApp
import com.kayadami.bouldering.data.type.Bouldering
import java.io.File

abstract class ImageLoader() {

    fun load(imageView: ImageView, bouldering: Bouldering): Int {
        GlideApp.with(imageView)
                .load(File(bouldering.thumb))
                .fitCenter()
                .signature(ObjectKey(bouldering.updatedAt.toString()))
                .into(imageView)

        return 0
    }

    abstract fun start()

    class ActivityImageLoader(
            val activity: Activity
    ) : ImageLoader() {
        override fun start() {
            Log.d("WTF", "ActivityImageLoader start")
        }
    }

    class FragmentImageLoader(
            val fragment: Fragment
    ) : ImageLoader() {
        override fun start() {
            Log.d("WTF", "FragmentImageLoader start")
        }
    }

    class ApplicationImageLoader(
            val context: Context
    ) : ImageLoader() {
        override fun start() {
            Log.d("WTF", "ApplicationImageLoader start")
        }
    }

    object Builder {
        fun create(
                activity: Activity
        ): ImageLoader {
            return ActivityImageLoader(activity)
        }

        fun create(
                fragment: Fragment
        ): ImageLoader {
            return FragmentImageLoader(fragment)
        }

        fun create(
                context: Context
        ): ImageLoader {
            return ApplicationImageLoader(context)
        }
    }
}