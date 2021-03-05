package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.editor.EditorResourceManager
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.BoulderingRepository
import com.kayadami.bouldering.data.PreferencesDataSource
import com.kayadami.bouldering.data.StorageDataSource
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
object ApplicationModule {

    @Provides
    @Singleton
    fun providePreferencesDataSource(
            @ApplicationContext context: Context
    ) = PreferencesDataSource(context)

    @Provides
    @Singleton
    fun provideStorageDataSource(
            @ApplicationContext context: Context
    ) = StorageDataSource(context)

    @Provides
    @Singleton
    fun provideBoulderingDataSource(
            preferences: PreferencesDataSource,
            storage: StorageDataSource
    ): BoulderingDataSource = BoulderingRepository(preferences, storage)

    @Provides
    @Singleton
    fun provideImageGenerator(
            @ApplicationContext context: Context
    ) = ImageGenerator(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

    @Provides
    @Singleton
    fun provideRoundedCornersTransformation(
            @ApplicationContext context: Context
    ) = RoundedCornersTransformation(
            context.resources.getDimension(R.dimen.list_round).toInt(),
            0
    )

    @Provides
    @Singleton
    fun provideEditorResourceManager(
            @ApplicationContext context: Context
    ) = EditorResourceManager(context)
}