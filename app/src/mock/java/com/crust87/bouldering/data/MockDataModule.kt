package com.crust87.bouldering.data

import android.content.Context
import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.MockDataInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MockDataModule {

    @Provides
    @Singleton
    fun provideMockDataInitializer(
        @ApplicationContext context: Context,
    ): MockDataInitializer {
        return MockDataInitializer(context)
    }

    @Provides
    @Singleton
    fun provideBoulderingRepository(
        mockDataInitializer: MockDataInitializer
    ): BoulderingRepository {
        return BoulderingRepository(mockDataInitializer)
    }
}