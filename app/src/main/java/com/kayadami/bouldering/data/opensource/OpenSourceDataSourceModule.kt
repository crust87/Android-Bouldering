package com.kayadami.bouldering.data.opensource

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface OpenSourceDataSourceModule {

    @Binds
    fun bindBoulderingDataSource(repository: OpenSourceRepository): OpenSourceDataSource
}