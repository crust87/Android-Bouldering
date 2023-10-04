package com.kayadami.bouldering.data.comment

import com.kayadami.bouldering.data.comment.type.Comment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    val commentDao: CommentDao
) {

    suspend fun getList(boulderingId: Long, page: Int): List<Comment> {
        return commentDao.getAllByBoulderingId(boulderingId, page)
    }

    suspend fun addComment(text: String, boulderingId: Long) {
        commentDao.insertAll(
            Comment(
                0,
                text.trim(),
                boulderingId,
                System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(commentId: Long) {
        commentDao.deleteById(commentId)
    }
}