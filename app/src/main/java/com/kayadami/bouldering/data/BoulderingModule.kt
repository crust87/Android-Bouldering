package com.kayadami.bouldering.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object BoulderingModule {

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
}