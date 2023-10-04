package com.kayadami.bouldering.data.comment

import com.kayadami.bouldering.data.comment.type.Comment

interface CommentDataSource {

    suspend fun getList(boulderingId: Long, page: Int): List<Comment>

    suspend fun addComment(text: String, boulderingId: Long)

    suspend fun delete(commentId: Long)
}