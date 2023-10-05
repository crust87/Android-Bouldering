package com.crust87.bouldering.image

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityImageLoaderModule {

    @ActivityImageLoader
    @Provides
    fun providerActivityImageLoader(
            activity: Activity
    ) = ImageLoader.Builder.create(
            activity
    )
}