package com.crust87.bouldering.app.viewer.comment

sealed interface CommentViewModelEvent

object OnNewCommentEvent: CommentViewModelEvent

object OnDeleteCommentEvent: CommentViewModelEvent