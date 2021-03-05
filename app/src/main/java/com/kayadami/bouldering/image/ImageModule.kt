package com.kayadami.bouldering.image

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.editor.ImageGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideRoundedCornersTransformation(
            @ApplicationContext context: Context
    ) = RoundedCornersTransformation(
            context.resources.getDimension(R.dimen.list_round).toInt(),
            0
    )
}