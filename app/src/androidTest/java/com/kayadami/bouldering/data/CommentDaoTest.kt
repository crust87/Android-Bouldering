package com.kayadami.bouldering.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kayadami.bouldering.constants.COMMENT_PAGE_LIMIT
import com.kayadami.bouldering.data.bouldering.type.BoulderingEntity
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

    @Before
    fun createDb() = runBlocking(Dispatchers.IO) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        val currentTime = System.currentTimeMillis()

        db.boulderingDao().insertAll(
            BoulderingEntity(
                TEST_BOULDERING_ID,
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

        db.commentDao().insertAll(
            Comment(
                TEST_COMMENT_ID,
                TEST_TEXT,
                TEST_BOULDERING_ID,
                currentTime
            )
        )[0]

        Unit
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
                TEST_BOULDERING_ID,
                currentTime
            )
        )

        val testData = db.commentDao().getAllByBoulderingId(TEST_BOULDERING_ID)

        Assert.assertEquals(2, testData.size)
    }

    @Test
    fun givenTestData_whenDelete_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.commentDao().get(TEST_COMMENT_ID)?.let {
            db.commentDao().delete(it)
        }

        val testData = db.commentDao().getAllByBoulderingId(TEST_BOULDERING_ID)

        Assert.assertEquals(0, testData.size)
    }

    @Test
    fun givenTestData_whenDeleteById_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.commentDao().deleteById(TEST_COMMENT_ID)

        val testData = db.commentDao().getAllByBoulderingId(TEST_BOULDERING_ID)

        Assert.assertEquals(0, testData.size)
    }

    @Test
    fun givenTestData_whenDeleteByBoulderingId_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.commentDao().deleteByBoulderingId(TEST_BOULDERING_ID)

        val testData = db.commentDao().getAllByBoulderingId(TEST_BOULDERING_ID)

        Assert.assertEquals(0, testData.size)
    }

    @Test
    fun givenTestData_whenPaging_thenResultLimited() = runBlocking(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()

        repeat(20) {
            db.commentDao().insertAll(
                Comment(
                    0,
                    TEST_TEXT,
                    TEST_BOULDERING_ID,
                    currentTime
                )
            )
        }

        val testData = db.commentDao().getAllByBoulderingId(TEST_BOULDERING_ID, 0)

        Assert.assertEquals(COMMENT_PAGE_LIMIT, testData.size)
    }

    companion object {
        const val TEST_BOULDERING_ID = 2000L
        const val TEST_COMMENT_ID = 1000L
        const val TEST_TEXT = "TEST_TEXT"
    }
}