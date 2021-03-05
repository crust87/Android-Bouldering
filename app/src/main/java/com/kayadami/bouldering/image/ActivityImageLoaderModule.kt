package com.kayadami.bouldering.image

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@Module
@InstallIn(ActivityComponent::class)
object ActivityImageLoaderModule {

    @ActivityImageLoader
    @Provides
    fun providerActivityImageLoader(
            activity: Activity,
            transformation: RoundedCornersTransformation
    ) = ImageLoader.Builder.create(
            activity,
            transformation
    )
}