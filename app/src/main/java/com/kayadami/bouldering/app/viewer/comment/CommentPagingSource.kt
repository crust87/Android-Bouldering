package com.kayadami.bouldering.app.viewer.comment

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kayadami.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.data.comment.type.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentPagingSource(
    val boulderingId: Long,
    val commentDao: CommentDao
) : PagingSource<Int, Comment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val key = params.key ?: 0

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

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        Log.d("WTF", "getRefreshKey")

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}