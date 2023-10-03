package com.kayadami.bouldering.app.main

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.app.MainFragmentComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
class MainFragmentModule {

    @MainFragmentComponent
    @Provides
    fun provideStaggeredGridLayoutManager() = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
}