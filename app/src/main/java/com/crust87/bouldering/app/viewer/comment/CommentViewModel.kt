package com.crust87.bouldering.app.viewer.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.crust87.bouldering.app.viewer.comment.domain.AddCommentUseCase
import com.crust87.bouldering.app.viewer.comment.domain.DeleteCommentUseCase
import com.crust87.bouldering.app.viewer.comment.domain.GetCommentPagerUseCase
import com.crust87.bouldering.app.viewer.comment.type.CommentItemUIState
import com.crust87.util.asDateText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentPagerUseCase: GetCommentPagerUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
) : ViewModel() {

    val comment = MutableLiveData<String>()

    val buttonEnabled = comment.map {
        !it.isNullOrBlank()
    }

    private var _boulderingId: Long = 0

    private var _pagerFlagWrapper = GetCommentPagerUseCase.PagerFlagWrapper(false)

    private val _eventChannel = MutableSharedFlow<CommentViewModelEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventChannel: SharedFlow<CommentViewModelEvent> = _eventChannel

    fun init(boulderingId: Long): Flow<PagingData<CommentItemUIState>> {
        _boulderingId = boulderingId

        return getCommentPagerUseCase(_boulderingId, _pagerFlagWrapper).flow.map { pagingData ->
            pagingData.map {
                CommentItemUIState(
                    it.id,
                    it.text,
                    it.createdAt.asDateText()
                ) {
                    deleteComment(it.id)
                }
            }
        }.cachedIn(viewModelScope)
    }

    fun addComment() = viewModelScope.launch {
        comment.value?.toString()?.let {
            if (it.isNotBlank()) {
                addCommentUseCase(it, _boulderingId)

                comment.value = ""

                _pagerFlagWrapper.needReset = true

                _eventChannel.tryEmit(OnNewCommentEvent)
            }
        }
    }

    private fun deleteComment(commentId: Long) = viewModelScope.launch {
        deleteCommentUseCase(commentId)

        _eventChannel.tryEmit(OnDeleteCommentEvent)
    }
}