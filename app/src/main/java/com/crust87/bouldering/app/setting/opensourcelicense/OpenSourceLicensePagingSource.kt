package com.crust87.bouldering.app.setting.opensourcelicense

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crust87.bouldering.data.opensource.OpenSourceRepository
import com.crust87.bouldering.data.opensource.type.OpenSourceLicense
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class OpenSourceLicensePagingSource @Inject constructor(
    val repository: OpenSourceRepository
) : PagingSource<Int, OpenSourceLicense>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpenSourceLicense> {
        val key = params.key ?: 0

        val data = if (key == 0) {
            repository.getList()
        } else {
            emptyList()
        }

        return if (data.isNotEmpty()) {
            LoadResult.Page(
                data = data,
                prevKey = key.minus(1),
                nextKey = key.plus(1)
            )
        } else {
            LoadResult.Error(RuntimeException("no more data"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, OpenSourceLicense>): Int {
        return 0
    }
}