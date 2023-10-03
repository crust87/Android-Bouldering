package com.kayadami.bouldering.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class BoulderingDaoTest {

    lateinit var db: AppDatabase

    @Before
    fun createDb() = runBlocking(Dispatchers.IO) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        val currentTime = System.currentTimeMillis()

        db.boulderingDao().insertAll(
            Bouldering(
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

        db.boulderingDao().insertAll(
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
        )

        val testData = db.boulderingDao().getAll()

        Assert.assertEquals(2, testData.size)
    }

    @Test
    fun givenTestData_whenUpdate_thenUpdated() = runBlocking(Dispatchers.IO) {
        db.boulderingDao().get(TEST_BOULDERING_ID)?.let {
            it.title = TEST_TITLE
            db.boulderingDao().update(it)
        }

        val currentTitle = db.boulderingDao().get(TEST_BOULDERING_ID)?.title

        Assert.assertEquals(TEST_TITLE, currentTitle)
    }

    @Test
    fun givenTestData_whenDelete_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.boulderingDao().get(TEST_BOULDERING_ID)?.let {
            db.boulderingDao().delete(it)
        }

        val testData = db.boulderingDao().getAll()

        Assert.assertEquals(0, testData.size)
    }

    @Test
    fun givenTestData_whenDeleteById_thenDeleted() = runBlocking(Dispatchers.IO) {
        db.boulderingDao().deleteById(TEST_BOULDERING_ID)

        val testData = db.boulderingDao().getAll()

        Assert.assertEquals(0, testData.size)
    }

    companion object {
        const val TEST_BOULDERING_ID = 2000L
        const val TEST_TITLE = "TEST_TITLE"
    }
}