package com.kayadami.bouldering.app.viewer.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kayadami.bouldering.app.MainDispatcher
import com.kayadami.bouldering.app.viewer.comment.domain.CommentAdditionUseCase
import com.kayadami.bouldering.app.viewer.comment.domain.CommentDeletionUseCase
import com.kayadami.bouldering.app.viewer.comment.domain.CommentPagerFactory
import com.kayadami.bouldering.data.comment.type.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentPagerFactory: CommentPagerFactory,
    private val commentAdditionUseCase: CommentAdditionUseCase,
    private val commentDeletionUseCase: CommentDeletionUseCase,
    @MainDispatcher val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

    val comment = MutableLiveData<String>()

    val buttonEnabled = comment.map {
        !it.isNullOrBlank()
    }

    private var _boulderingId: Long = 0

    private var _pagerFlagWrapper = CommentPagerFactory.PagerFlagWrapper(false)

    private val _eventChannel = MutableSharedFlow<CommentViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventChannel: SharedFlow<CommentViewModelEvent> = _eventChannel

    fun init(boulderingId: Long): Flow<PagingData<Comment>> {
        _boulderingId = boulderingId

        return commentPagerFactory.create(_boulderingId, _pagerFlagWrapper).flow.cachedIn(viewModelScope)
    }

    fun addComment() = viewModelScope.launch(mainDispatcher) {
        comment.value?.toString()?.let {
            if (it.isNotBlank()) {
                commentAdditionUseCase(it, _boulderingId)

                comment.value = ""

                _pagerFlagWrapper.needReset = true

                _eventChannel.tryEmit(OnNewCommentEvent)
            }
        }
    }

    fun deleteComment(commentId: Long) = viewModelScope.launch(mainDispatcher) {
        commentDeletionUseCase(commentId)

        _eventChannel.tryEmit(OnDeleteCommentEvent)
    }
}