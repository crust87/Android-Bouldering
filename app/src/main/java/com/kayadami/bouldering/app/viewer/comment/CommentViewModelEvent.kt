package com.kayadami.bouldering.app.viewer.comment

sealed interface CommentViewModelEvent

object OnNewCommentEvent: CommentViewModelEvent

object OnDeleteCommentEvent: CommentViewModelEvent