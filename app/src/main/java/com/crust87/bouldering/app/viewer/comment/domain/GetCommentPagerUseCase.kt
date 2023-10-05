package com.crust87.bouldering.app.viewer.comment.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crust87.bouldering.data.COMMENT_PAGE_LIMIT
import com.crust87.bouldering.data.comment.CommentRepository
import com.crust87.bouldering.data.comment.type.Comment
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetCommentPagerUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(boulderingId: Long, pagerFlagWrapper: PagerFlagWrapper): Pager<Int, Comment> {
        return Pager(
            config = PagingConfig(
                pageSize = COMMENT_PAGE_LIMIT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommentPagingSource(boulderingId, pagerFlagWrapper) }
        )
    }

    private inner class CommentPagingSource(
        val boulderingId: Long,
        val pagerFlagWrapper: PagerFlagWrapper
    ) : PagingSource<Int, Comment>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
            val key = params.key ?: 0

            if (key < 0) {
                return LoadResult.Error(RuntimeException("no page under 0"))
            }

            val data = commentRepository.getList(boulderingId, key)

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

        override fun getRefreshKey(state: PagingState<Int, Comment>): Int {
            return if (pagerFlagWrapper.needReset) {
                pagerFlagWrapper.needReset = false

                0
            } else {
                state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
                } ?: 0
            }
        }
    }

    class PagerFlagWrapper(var needReset: Boolean)
}