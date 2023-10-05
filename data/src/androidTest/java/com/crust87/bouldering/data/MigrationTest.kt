package com.crust87.bouldering.data

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.crust87.bouldering.data.Migration.MIGRATION_2_3
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        ).addMigrations(*Migration.asArray()).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        helper.createDatabase(TEST_DB, 2).apply {
            execSQL("INSERT INTO `comment` (id, text, boulderingId) VALUES(0, '$TEST_TEXT', 1)")

            close()
        }

        val database = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        val cursor = database.query("SELECT * FROM `comment`").apply {
            moveToFirst()
        }

        val id = cursor.getLong(0)
        val text = cursor.getString(1)
        val boulderingId = cursor.getLong(2)
        val createdAt = cursor.getLong(3)

        Assert.assertEquals(0, id)
        Assert.assertEquals(TEST_TEXT, text)
        Assert.assertEquals(1, boulderingId)
        Assert.assertTrue(createdAt > 0)
    }

    companion object {
        const val TEST_DB = "migration-test"
        const val TEST_TEXT = "TEST_TEXT"
    }
}