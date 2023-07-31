package com.kayadami.bouldering.app.main

import android.content.res.Resources
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kayadami.bouldering.R
import com.kayadami.bouldering.app.MainFragmentComponent
import com.kayadami.bouldering.list.GridSpacingItemDecoration
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

    @MainFragmentComponent
    @Provides
    fun provideGridSpacingItemDecoration(resources: Resources) = GridSpacingItemDecoration(2).apply {
        spacing = resources.getDimension(R.dimen.list_spacing).toInt()
    }
}