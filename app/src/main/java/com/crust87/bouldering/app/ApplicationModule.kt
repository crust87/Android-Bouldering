package com.crust87.bouldering.app

import android.content.Context
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.crust87.bouldering.editor.ImageGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideWindowManager(@ApplicationContext context: Context): WindowManager {
        return ContextCompat.getSystemService(context, WindowManager::class.java)!!
    }

    @Provides
    @Singleton
    fun provideImageGenerator(windowManager: WindowManager): ImageGenerator {
        return ImageGenerator(windowManager)
    }

    @Provides
    @Singleton
    fun provideResources(
        @ApplicationContext context: Context
    ) = context.resources

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ) = context.contentResolver

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}