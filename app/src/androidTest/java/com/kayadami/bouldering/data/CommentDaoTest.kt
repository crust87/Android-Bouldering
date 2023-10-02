package com.kayadami.bouldering.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.data.comment.type.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CommentDaoTest {

    lateinit var db: AppDatabase

    var testBoulderingId: Long = 0
    var testCommentId: Long = 0

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        val currentTime = System.currentTimeMillis()

        testBoulderingId = db.boulderingDao().insertAll(
            Bouldering(
                0,
                "",
                "",
                "",
                false,
                null,
                currentTime,
                currentTime,
                emptyList(),
                0
            )
        )[0]

        testCommentId = db.commentDao().insertAll(
            Comment(
                0,
                TEST_TEXT,
                testBoulderingId,
                currentTime
            )
        )[0]
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun givenTestData_whenInsert_thenInserted() = runBlocking(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()

        db.commentDao().insertAll(
            Comment(
                0,
                TEST_TEXT,
                testBoulderingId,
                currentTime
            )
        )

        val testData = db.commentDao().getAllByBoulderingId(testBoulderingId)

        Assert.assertEquals(2, testData.size)
    }

    @Test
    fun givenTestData_whenDelete_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.commentDao().get(testBoulderingId)?.let {
            db.commentDao().delete(it)
        }

        val testData = db.commentDao().getAllByBoulderingId(testBoulderingId)

        Assert.assertEquals(0, testData.size)
    }

    @Test
    fun givenTestData_whenDeleteById_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.commentDao().deleteById(testBoulderingId)

        val testData = db.commentDao().getAllByBoulderingId(testBoulderingId)

        Assert.assertEquals(0, testData.size)
    }

    companion object {
        const val TEST_TEXT = "TEST_TEXT"
    }
}