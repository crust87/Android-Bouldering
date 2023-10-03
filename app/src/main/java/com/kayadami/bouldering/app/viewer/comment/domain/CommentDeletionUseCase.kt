package com.kayadami.bouldering.app.viewer.comment.domain

import com.kayadami.bouldering.data.comment.CommentDao
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CommentDeletionUseCase @Inject constructor(val commentDao: CommentDao) {

    suspend operator fun invoke(commentId: Long) {
        commentDao.deleteById(commentId)
    }
}