package com.crust87.bouldering.app.viewer.comment.type

data class CommentItemUIState(
    val id: Long,
    val text: String,
    val createdAt: String,
    val delete: () -> Unit,
)