package com.kayadami.bouldering.app.viewer.comment.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kayadami.bouldering.constants.COMMENT_PAGE_LIMIT
import com.kayadami.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.data.comment.type.Comment
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class CommentPagerFactory @Inject constructor(private val commentDao: CommentDao) {

    fun create(boulderingId: Long): Pager<Int, Comment> {
        return Pager(
            config = PagingConfig(
                pageSize = COMMENT_PAGE_LIMIT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommentPagingSource(boulderingId) }
        )
    }

    private inner class CommentPagingSource(
        val boulderingId: Long,
    ) : PagingSource<Int, Comment>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
            val key = params.key ?: 0

            if (key < 0) {
                return LoadResult.Error(RuntimeException("no more data"))
            }

            val data = withContext(Dispatchers.IO) {
                commentDao.getAllByBoulderingId(boulderingId, key)
            }

            return if (data.isNotEmpty()) {
                LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = key.plus(1)
                )
            } else {
                LoadResult.Error(RuntimeException("no more data"))
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Comment>): Int {
            return 0
        }
    }
}