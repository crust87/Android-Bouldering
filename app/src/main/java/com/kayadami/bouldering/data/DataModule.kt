package com.kayadami.bouldering.data

import android.content.Context
import androidx.room.Room
import com.crust87.bouldering.data.AppDatabase
import com.crust87.bouldering.data.Migration
import com.crust87.bouldering.data.comment.CommentDao
import com.crust87.bouldering.data.comment.CommentRepository
import com.crust87.bouldering.data.opensource.OpenSourceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-bouldering"
        ).addMigrations(*Migration.asArray()).build()
    }

    @Provides
    fun provideBoulderingDao(database: AppDatabase) = database.boulderingDao()

    @Provides
    fun provideCommentDao(database: AppDatabase) = database.commentDao()

    @Provides
    @Singleton
    fun provideCommentRepository(commentDao: CommentDao): CommentRepository {
        return CommentRepository(commentDao)
    }

    @Provides
    @Singleton
    fun provideOpenSourceRepository(): OpenSourceRepository {
        return OpenSourceRepository()
    }
}