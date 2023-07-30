package com.kayadami.bouldering.image

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApplicationImageLoaderModule {

    @ApplicationImageLoader
    @Provides
    fun providerApplicationImageLoader(
            @ApplicationContext context: Context
    ) = ImageLoader.Builder.create(
            context
    )
}