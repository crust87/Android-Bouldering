package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import com.kayadami.bouldering.InstantExecutorListener
import com.kayadami.bouldering.MainDispatcherListener
import com.kayadami.bouldering.data.BoulderingRepository
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.Options
import com.kayadami.bouldering.getOrAwaitValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class EditorViewModelTest : BehaviorSpec({
    listener(InstantExecutorListener())
    listener(MainDispatcherListener())

    val repository = mockk<BoulderingRepository>()
    val resources = mockk<Resources>()

    given("홀더가 선택되어 있지 않으면") {
        val viewModel = EditorViewModel(repository, resources, UnconfinedTestDispatcher(), UnconfinedTestDispatcher())

        viewModel.setHolder(null)

        then("Problem Tool UI가 보이고, Holder Tool UI는 안보인다") {
            viewModel.uiState.getOrAwaitValue()?.problemToolVisibility shouldBe View.VISIBLE
            viewModel.uiState.getOrAwaitValue()?.holderToolVisibility shouldBe View.GONE
        }
    }

    given("홀더가 선택되어 있으면") {
        val viewModel = EditorViewModel(repository, resources, UnconfinedTestDispatcher(), UnconfinedTestDispatcher())

        viewModel.setHolder(HolderBox(Options()))

        then("Problem Tool UI가 안보이고, Holder Tool UI는 보인다") {
            viewModel.uiState.getOrAwaitValue()?.problemToolVisibility shouldBe View.GONE
            viewModel.uiState.getOrAwaitValue()?.holderToolVisibility shouldBe View.VISIBLE
        }

        and("선택된 홀더가 일반 홀더이면") {
            viewModel.setHolder(HolderBox(Options()))

            then("순서 속성이 아니며, 특수 속성도 아니며, 숫자 속성으로 활성화/비활성화 할 수 있다") {
                viewModel.uiState.getOrAwaitValue()?.isNumberHolder shouldBe false
                viewModel.uiState.getOrAwaitValue()?.isSpecialHolder shouldBe false
                viewModel.uiState.getOrAwaitValue()?.isNumberEnabled shouldBe true
            }
        }

        and("선택된 홀더가 특수 속성 홀더이면") {
            viewModel.setHolder(HolderBox(Options()).apply {
                isSpecial = true
            })

            then("순서 속성은 아니며, 특수 속성 이면서, 숫자 속성으로 활성화/비활성화 할 수 없다") {
                viewModel.uiState.getOrAwaitValue()?.isNumberHolder shouldBe false
                viewModel.uiState.getOrAwaitValue()?.isSpecialHolder shouldBe true
                viewModel.uiState.getOrAwaitValue()?.isNumberEnabled shouldBe false
            }
        }

        and("선택된 홀더가 순서 속성 홀더이면") {
            viewModel.setHolder(HolderBox(Options()).apply {
                isInOrder = true
            })

            then("순서 속성이 이면서, 특수 속성은 아니며, 숫자 속성으로 활성화/비활성화 할 수 있다") {
                viewModel.uiState.getOrAwaitValue()?.isNumberHolder shouldBe true
                viewModel.uiState.getOrAwaitValue()?.isSpecialHolder shouldBe false
                viewModel.uiState.getOrAwaitValue()?.isNumberEnabled shouldBe true
            }
        }
    }
})

