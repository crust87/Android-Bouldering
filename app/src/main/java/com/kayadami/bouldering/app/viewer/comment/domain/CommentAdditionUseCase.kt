package com.kayadami.bouldering.app.viewer.comment.domain

import com.kayadami.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.data.comment.type.Comment
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CommentAdditionUseCase @Inject constructor(val commentDao: CommentDao) {

    suspend operator fun invoke(text: String?, boulderingId: Long): Boolean {
        if (!text.isNullOrBlank()) {
            commentDao.insertAll(
                Comment(
                    0,
                    text.trim(),
                    boulderingId,
                    System.currentTimeMillis()
                )
            )

            return true
        }

        return false
    }
}