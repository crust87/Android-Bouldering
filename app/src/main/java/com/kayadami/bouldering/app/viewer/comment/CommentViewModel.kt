package com.kayadami.bouldering.app.viewer.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kayadami.bouldering.app.viewer.comment.domain.CommentAdditionUseCase
import com.kayadami.bouldering.app.viewer.comment.domain.CommentDeletionUseCase
import com.kayadami.bouldering.app.viewer.comment.domain.CommentPagerFactory
import com.kayadami.bouldering.data.comment.type.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentPagerFactory: CommentPagerFactory,
    private val commentAdditionUseCase: CommentAdditionUseCase,
    private val commentDeletionUseCase: CommentDeletionUseCase,
) : ViewModel() {

    val comment = MutableLiveData<String>()

    val buttonEnabled = comment.map {
        !it.isNullOrBlank()
    }

    private var _boulderingId: Long = 0

    val eventChannel = MutableSharedFlow<CommentViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )

    fun init(boulderingId: Long): Flow<PagingData<Comment>> {
        _boulderingId = boulderingId

        return commentPagerFactory.create(_boulderingId).flow.cachedIn(viewModelScope)
    }

    fun addComment() = viewModelScope.launch(Dispatchers.Main) {
        val text = comment.value?.toString()

        if (commentAdditionUseCase(text, _boulderingId)) {
            comment.value = ""

            eventChannel.tryEmit(OnNewCommentEvent)
        }
    }

    fun deleteComment(commentId: Long) = viewModelScope.launch(Dispatchers.Main) {
        commentDeletionUseCase(commentId)

        eventChannel.tryEmit(OnDeleteCommentEvent)
    }
}