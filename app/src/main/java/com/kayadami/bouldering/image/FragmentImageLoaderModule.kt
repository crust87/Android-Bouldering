package com.kayadami.bouldering.image

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FragmentImageLoaderModule {

    @FragmentImageLoader
    @Provides
    fun providerFragmentImageLoader(
            fragment: Fragment
    ) = ImageLoader.Builder.create(
            fragment
    )
}