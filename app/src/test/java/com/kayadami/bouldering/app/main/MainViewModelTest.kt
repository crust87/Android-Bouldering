package com.kayadami.bouldering.app.main

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kayadami.bouldering.TestUtils
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.data.type.Bouldering
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val application: Application = mock(Application::class.java)
    private val repository: BoulderingDataSource = mock(BoulderingDataSource::class.java)

    private lateinit var mainViewModel: MainViewModel

    private val mockBoulderingCount = 5

    @Before
    fun setUp() {
        ArrayList<Bouldering>().apply {
            repeat(mockBoulderingCount) {
                add(mock(Bouldering::class.java).apply {
                    `when`(createdDate).thenReturn(it.toLong())
                })
            }
        }.also {
            `when`(repository.get()).thenReturn(it)
        }

        mainViewModel = MainViewModel(application, repository)
    }

    @Test
    fun givenIndex_whenGetBouldering_thenReturnByIndex() {
        val testIndex = 2
        val bouldering = mainViewModel[testIndex]

        assertEquals(testIndex.toLong(), bouldering.createdDate)
    }

    @Test
    fun givenMockArray_whenCheckEmpty_thenReturnTrue() {
        val isNotEmpty = mainViewModel.isNotEmpty()

        assertTrue(isNotEmpty)
    }

    @Test
    fun givenEmptyArray_whenCheckEmpty_thenReturnFalse() {
        mainViewModel.repository.get().clear()
        val isNotEmpty = mainViewModel.isNotEmpty()

        assertFalse(isNotEmpty)
    }

    @Test
    fun whenCheckCount_thenReturnBoulderingCount() {
        val count = mainViewModel.size

        assertEquals(mockBoulderingCount, count)
    }

    @Test
    fun whenCallOpenCamera() {
        val observer = com.kayadami.bouldering.mock<Observer<Void>>()

        with(mainViewModel) {
            openCameraEvent.observe(TestUtils.TEST_OBSERVER, observer)
            openCamera()
        }

        verify<Observer<Void>>(observer).onChanged(null)
    }

    @Test
    fun whenCallOpenGallery() {
        val observer = com.kayadami.bouldering.mock<Observer<Void>>()

        with(mainViewModel) {
            openGalleryEvent.observe(TestUtils.TEST_OBSERVER, observer)
            openGallery()
        }

        verify<Observer<Void>>(observer).onChanged(null)
    }
}