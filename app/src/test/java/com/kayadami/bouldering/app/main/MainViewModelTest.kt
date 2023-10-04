package com.kayadami.bouldering.app.main

import com.kayadami.bouldering.InstantExecutorListener
import com.kayadami.bouldering.MainDispatcherListener
import com.kayadami.bouldering.app.domain.OpenCameraUseCase
import com.kayadami.bouldering.app.domain.OpenGalleryUseCase
import com.kayadami.bouldering.app.main.type.EmptyListItem
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.data.bouldering.type.Bouldering
import com.kayadami.bouldering.getOrAwaitValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : BehaviorSpec({
    listener(InstantExecutorListener())
    listener(MainDispatcherListener())

    val boulderingDataSource = mockk<BoulderingDataSource>()
    val openCameraUseCase = mockk<OpenCameraUseCase>()
    val openGalleryUseCase = mockk<OpenGalleryUseCase>()

    given("저장된 리스트가 있는 경우에는") {
        every { boulderingDataSource.list(any()) } returns flow {
            emit((1L..10L).map {
                Bouldering(
                    it,
                    "",
                    "",
                    "",
                    false,
                    null,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    emptyList(),
                    0
                )
            })
        }

        val viewModel = MainViewModel(boulderingDataSource, openCameraUseCase, openGalleryUseCase)

        then("MainListItem으로 Wrapping된 리스트가 생성된다") {
            viewModel.list.getOrAwaitValue()?.size shouldBe 10
        }
    }

    given("저장된 리스트가 없는 경우에는") {
        every { boulderingDataSource.list(any()) } returns flow {
            emit(emptyList())
        }

        val viewModel = MainViewModel(boulderingDataSource, openCameraUseCase, openGalleryUseCase)

        then("EmptyListItem 1개를 가진 리스트가 생성된다") {
            val resultList = viewModel.list.getOrAwaitValue()

            resultList?.size shouldBe 1
            resultList?.getOrNull(0) should beInstanceOf<EmptyListItem>()
        }
    }
})