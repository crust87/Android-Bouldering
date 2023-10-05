package com.kayadami.bouldering.app.viewer.comment.domain

import com.crust87.bouldering.data.comment.CommentRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AddCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(text: String, boulderingId: Long) {
        commentRepository.addComment(
            text.trim(),
            boulderingId
        )
    }
}