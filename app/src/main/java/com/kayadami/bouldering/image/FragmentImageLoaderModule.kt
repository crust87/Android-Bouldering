package com.kayadami.bouldering.image

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

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