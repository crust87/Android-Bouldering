package com.kayadami.bouldering.app

import android.content.Context
import android.view.WindowManager
import androidx.core.content.ContextCompat
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
    fun provideWindowManager(@ApplicationContext context: Context): WindowManager {
        return ContextCompat.getSystemService(context, WindowManager::class.java)!!
    }

    @Provides
    @Singleton
    fun providePermissionChecker(
            @ApplicationContext context: Context
    ) = PermissionChecker2(context)
}