package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.app.editor.EditorResourceManager
import com.kayadami.bouldering.editor.ImageGenerator
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

    @Provides
    @Singleton
    fun provideImageGenerator(
            @ApplicationContext context: Context
    ) = ImageGenerator(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
}