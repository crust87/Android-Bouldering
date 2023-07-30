package com.kayadami.bouldering.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BoulderingDataSourceModule {

    @Provides
    @Singleton
    fun provideBoulderingDataSource(
            preferences: PreferencesDataSource,
            storage: StorageDataSource
    ): BoulderingDataSource = BoulderingRepository(preferences, storage)
}