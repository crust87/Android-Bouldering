package com.kayadami.bouldering.app.editor

import android.content.res.Resources
import android.view.View
import com.kayadami.bouldering.InstantExecutorListener
import com.kayadami.bouldering.data.bouldering.BoulderingDataSource
import com.kayadami.bouldering.editor.HolderBox
import com.kayadami.bouldering.editor.Options
import com.kayadami.bouldering.getOrAwaitValue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class EditorViewModelTest : BehaviorSpec({
    listener(InstantExecutorListener())

    val repository = mockk<BoulderingDataSource>()
    val resources = mockk<Resources>()

    val viewModel = EditorViewModel(repository, resources)

    given("홀더가 선택되어 있지 않으면") {
        viewModel.selectedHolder.value = null

        then("Problem Tool UI가 보이고") {
            viewModel.problemToolVisibility.getOrAwaitValue() shouldBe View.VISIBLE
        }

        then("Holder Tool UI는 안보인다") {
            viewModel.holderToolVisibility.getOrAwaitValue() shouldBe View.GONE
        }
    }

    given("홀더가 선택되어 있으면") {
        viewModel.selectedHolder.value = HolderBox(Options())

        then("Problem Tool UI가 안보이고") {
            viewModel.problemToolVisibility.getOrAwaitValue() shouldBe View.GONE
        }

        then("Holder Tool UI는 보인다") {
            viewModel.holderToolVisibility.getOrAwaitValue() shouldBe View.VISIBLE
        }

        and("선택된 홀더가 일반 홀더이면") {
            viewModel.selectedHolder.value = HolderBox(Options())

            then("순서 속성이 아니며") {
                viewModel.isNumberHolder.getOrAwaitValue() shouldBe false
            }

            then("특수 속성도 아니며") {
                viewModel.isSpecialHolder.getOrAwaitValue() shouldBe false
            }

            then("숫자 속성으로 활성화/비활성화 할 수 있다") {
                viewModel.isNumberEnabled.getOrAwaitValue() shouldBe true
            }
        }

        and("선택된 홀더가 특수 속성 홀더이면") {
            viewModel.selectedHolder.value = HolderBox(Options()).apply {
                isSpecial = true
            }

            then("순서 속성은 아니며") {
                viewModel.isNumberHolder.getOrAwaitValue() shouldBe false
            }

            then("특수 속성 이면서") {
                viewModel.isSpecialHolder.getOrAwaitValue() shouldBe true
            }

            then("숫자 속성으로 활성화/비활성화 할 수 없다") {
                viewModel.isNumberEnabled.getOrAwaitValue() shouldBe false
            }
        }

        and("선택된 홀더가 순서 속성 홀더이면") {
            viewModel.selectedHolder.value = HolderBox(Options()).apply {
                isInOrder = true
            }

            then("순서 속성이 이면서") {
                viewModel.isNumberHolder.getOrAwaitValue() shouldBe true
            }

            then("특수 속성은 아니며") {
                viewModel.isSpecialHolder.getOrAwaitValue() shouldBe false
            }

            then("숫자 속성으로 활성화/비활성화 할 수 있다") {
                viewModel.isNumberEnabled.getOrAwaitValue() shouldBe true
            }
        }
    }
})

