package com.crust87.bouldering.data

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.crust87.bouldering.data.comment.CommentDao
import com.crust87.bouldering.data.comment.CommentRepository
import com.crust87.bouldering.data.opensource.BoulderingService
import com.crust87.bouldering.data.opensource.OpenSourceRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
    fun provideApiClient(@ApplicationContext context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://crust87.github.io/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideBoulderingService(retrofit: Retrofit): BoulderingService {
        return retrofit.create(BoulderingService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenSourceRepository(boulderingService: BoulderingService): OpenSourceRepository {
        return OpenSourceRepository(boulderingService)
    }
}