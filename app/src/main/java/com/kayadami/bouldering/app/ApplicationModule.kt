package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import com.kayadami.bouldering.app.editor.EditorResourceManager
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.utils.PermissionChecker2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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

    @Provides
    @Singleton
    fun providePermissionChecker(
            @ApplicationContext context: Context
    ) = PermissionChecker2(context)
}