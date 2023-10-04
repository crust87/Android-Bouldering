package com.kayadami.bouldering.app.viewer.comment.domain

import com.kayadami.bouldering.data.comment.CommentDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddCommentUseCase @Inject constructor(
    private val commentDataSource: CommentDataSource
) {

    suspend operator fun invoke(text: String, boulderingId: Long) {
        commentDataSource.addComment(
            text.trim(),
            boulderingId
        )
    }
}