package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kayadami.bouldering.data.BoulderingDataSource
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.Options
import com.kayadami.bouldering.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class EditorViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: EditorViewModel

    @Before
    fun setUp() {
        val repository = mock(BoulderingDataSource::class.java)
        val resources = mock(Resources::class.java)

        viewModel = EditorViewModel(repository, resources)
    }

    @Test
    fun givenNoHolderSelected_thenProblemToolIsVisible() {
        viewModel.selectedHolder.value = null

        val problemToolVisibility = viewModel.problemToolVisibility.getOrAwaitValue()
        val holderToolVisibility = viewModel.holderToolVisibility.getOrAwaitValue()

        Assert.assertEquals(View.VISIBLE, problemToolVisibility)
        Assert.assertEquals(View.GONE, holderToolVisibility)
    }

    @Test
    fun givenHolderSelected_thenHolderToolIsVisible() {
        viewModel.selectedHolder.value = HolderBox(Options())

        val problemToolVisibility = viewModel.problemToolVisibility.getOrAwaitValue()
        val holderToolVisibility = viewModel.holderToolVisibility.getOrAwaitValue()

        Assert.assertEquals(View.GONE, problemToolVisibility)
        Assert.assertEquals(View.VISIBLE, holderToolVisibility)
    }

    @Test
    fun givenNormalHolder() {
        viewModel.selectedHolder.value = HolderBox(Options())

        val isNumberHolder = viewModel.isNumberHolder.getOrAwaitValue()
        val isSpecialHolder = viewModel.isSpecialHolder.getOrAwaitValue()

        Assert.assertEquals(false, isNumberHolder)
        Assert.assertEquals(false, isSpecialHolder)
    }

    @Test
    fun givenSpecialHolder() {
        viewModel.selectedHolder.value = HolderBox(Options()).apply {
            isSpecial = true
        }

        val value = viewModel.isSpecialHolder.getOrAwaitValue()

        val isNumberHolder = viewModel.isNumberHolder.getOrAwaitValue()
        val isSpecialHolder = viewModel.isSpecialHolder.getOrAwaitValue()

        Assert.assertEquals(false, isNumberHolder)
        Assert.assertEquals(true, isSpecialHolder)
    }

    @Test
    fun givenOrderHolder() {
        viewModel.selectedHolder.value = HolderBox(Options()).apply {
            isInOrder = true
        }

        val isNumberHolder = viewModel.isNumberHolder.getOrAwaitValue()
        val isSpecialHolder = viewModel.isSpecialHolder.getOrAwaitValue()

        Assert.assertEquals(true, isNumberHolder)
        Assert.assertEquals(false, isSpecialHolder)
    }
}