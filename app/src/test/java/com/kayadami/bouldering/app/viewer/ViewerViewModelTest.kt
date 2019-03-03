package com.kayadami.bouldering.app.viewer

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kayadami.bouldering.app.main.MainViewModel
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.ImageGenerator
import com.kayadami.bouldering.editor.data.Bouldering
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class ViewerViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val application: Application = Mockito.mock(Application::class.java)
    private val repository: BoulderingDataSource = Mockito.mock(BoulderingDataSource::class.java)
    private val imageGenerator: ImageGenerator = Mockito.mock(ImageGenerator::class.java)

    lateinit var viewViewModel: ViewerViewModel

    private val mockBoulderingCount = 5

    @Before
    fun setUp() {
        ArrayList<Bouldering>().apply {
            repeat(mockBoulderingCount) {
                add(Mockito.mock(Bouldering::class.java).apply {
                    Mockito.`when`(createdDate).thenReturn(it.toLong())
                })
            }
        }.also {
            Mockito.`when`(repository.get()).thenReturn(it)
        }

        viewViewModel = ViewerViewModel(application, repository, imageGenerator)
    }

    @Test
    fun whenUpdate() {

    }
}