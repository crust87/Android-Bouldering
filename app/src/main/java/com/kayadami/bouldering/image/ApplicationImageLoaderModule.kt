package com.kayadami.bouldering.image

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
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