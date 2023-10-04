package com.kayadami.bouldering.app.viewer.comment.domain

import com.kayadami.bouldering.data.comment.CommentDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteCommentUseCase @Inject constructor(
    private val commentDataSource: CommentDataSource
) {

    suspend operator fun invoke(commentId: Long) {
        commentDataSource.delete(commentId)
    }
}