package com.kayadami.bouldering.data.comment

import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.comment.type.Comment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    val commentDao: CommentDao
) : CommentDataSource {

    override suspend fun getList(boulderingId: Long, page: Int): List<Comment> {
        return commentDao.getAllByBoulderingId(boulderingId, page)
    }

    override suspend fun addComment(text: String, boulderingId: Long) {
        commentDao.insertAll(
            Comment(
                0,
                text.trim(),
                boulderingId,
                System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(commentId: Long) {
        commentDao.deleteById(commentId)
    }
}