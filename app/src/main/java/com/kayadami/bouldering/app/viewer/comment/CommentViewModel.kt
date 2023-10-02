package com.kayadami.bouldering.app.viewer.comment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kayadami.bouldering.app.viewer.comment.domain.CommentAdditionUseCase
import com.kayadami.bouldering.app.viewer.comment.domain.CommentPagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentPagerFactory: CommentPagerFactory,
    private val commentAdditionUseCase: CommentAdditionUseCase,
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

    fun init(boulderingId: Long) {
        _boulderingId = boulderingId
    }

    fun getList() = commentPagerFactory.create(_boulderingId).flow.cachedIn(viewModelScope)

    fun addComment() = viewModelScope.launch(Dispatchers.Main) {
        val text = comment.value?.toString()

        if (commentAdditionUseCase(text, _boulderingId)) {
            comment.value = ""
            eventChannel.tryEmit(OnNewCommentEvent)
        }
    }
}