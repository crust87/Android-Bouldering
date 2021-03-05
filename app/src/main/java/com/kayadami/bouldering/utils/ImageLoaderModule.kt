package com.kayadami.bouldering.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationImageLoaderModule {

    @ApplicationImageLoader
    @Provides
    fun providerApplicationImageLoader(
            @ApplicationContext context: Context,
            transformation: RoundedCornersTransformation
    ) = ImageLoader.Builder.create(
            context,
            transformation
    )
}

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

@Module
@InstallIn(FragmentComponent::class)
object FragmentImageLoaderModule {

    @FragmentImageLoader
    @Provides
    fun providerFragmentImageLoader(
            fragment: Fragment,
            transformation: RoundedCornersTransformation
    ) = ImageLoader.Builder.create(
            fragment,
            transformation
    )
}