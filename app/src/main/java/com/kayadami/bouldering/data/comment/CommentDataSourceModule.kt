package com.kayadami.bouldering.data.comment

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CommentDataSourceModule {

    @Binds
    fun bindCommentDataSource(commentRepository: CommentRepository): CommentDataSource
}