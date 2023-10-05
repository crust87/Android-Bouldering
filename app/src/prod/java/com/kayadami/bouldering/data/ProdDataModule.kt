package com.kayadami.bouldering.data

import com.crust87.bouldering.data.BoulderingRepository
import com.crust87.bouldering.data.bouldering.BoulderingDao
import com.crust87.bouldering.data.comment.CommentDao
import com.kayadami.bouldering.app.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProdDataModule {

    @Provides
    @Singleton
    fun provideBoulderingRepository(
        boulderingDao: BoulderingDao,
        commentDao: CommentDao,
        @MainDispatcher mainDispatcher: CoroutineDispatcher
    ): BoulderingRepository {
        return BoulderingRepository(boulderingDao, commentDao, mainDispatcher)
    }
}