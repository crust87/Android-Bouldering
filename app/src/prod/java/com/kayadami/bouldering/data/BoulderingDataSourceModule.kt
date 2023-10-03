package com.kayadami.bouldering.data

import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BoulderingDataSourceModule {

    @Binds
    fun bindBoulderingDataSource(boulderingRepository: BoulderingRepository): BoulderingDataSource
}