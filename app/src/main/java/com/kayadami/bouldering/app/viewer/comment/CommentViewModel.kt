package com.kayadami.bouldering.app.viewer.comment

import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kayadami.bouldering.constants.COMMENT_PAGE_LIMIT
import com.kayadami.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.data.comment.type.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    val commentDao: CommentDao,
) : ViewModel() {

    val comment = MutableLiveData<String>()

    val buttonEnabled = comment.map {
        !it.isNullOrBlank()
    }

    var _boulderingId: Long = 0

    fun init(boulderingId: Long) {
        _boulderingId = boulderingId
    }

    fun getList() = Pager(
        config = PagingConfig(
            pageSize = COMMENT_PAGE_LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { CommentPagingSource(_boulderingId, commentDao) }
    ).flow.cachedIn(viewModelScope)

    fun addComment() = viewModelScope.launch(Dispatchers.Main) {
        val text = comment.value?.toString()

        if (!text.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                commentDao.insertAll(
                    Comment(
                        0,
                        text.trim(),
                        _boulderingId,
                        System.currentTimeMillis()
                    )
                )
            }

            comment.value = ""
        }
    }
}