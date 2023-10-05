package com.crust87.bouldering.app.main

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.crust87.bouldering.app.MainFragmentComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
class MainFragmentModule {

    @Provides
    @MainFragmentComponent
    fun provideStaggeredGridLayoutManager() = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
}