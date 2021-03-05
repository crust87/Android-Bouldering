package com.kayadami.bouldering.app

import android.content.Context
import com.kayadami.bouldering.app.editor.EditorResourceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideEditorResourceManager(
            @ApplicationContext context: Context
    ) = EditorResourceManager(context)
}