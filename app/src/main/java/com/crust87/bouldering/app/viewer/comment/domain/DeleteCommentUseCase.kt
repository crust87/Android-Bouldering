package com.crust87.bouldering.app.viewer.comment.domain

import com.crust87.bouldering.data.comment.CommentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DeleteCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(commentId: Long) {
        commentRepository.delete(commentId)
    }
}