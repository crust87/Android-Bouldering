package com.crust87.bouldering.image

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.signature.ObjectKey
import com.crust87.bouldering.data.bouldering.type.BoulderingEntity
import com.crust87.bouldering.app.GlideApp
import java.io.File

abstract class ImageLoader() {

    fun load(imageView: ImageView, thumb: String, updatedAt: Long): Int {
        GlideApp.with(imageView)
                .load(File(thumb))
                .fitCenter()
                .signature(ObjectKey(updatedAt.toString()))
                .into(imageView)

        return 0
    }

    fun load(imageView: ImageView, bouldering: BoulderingEntity): Int {
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