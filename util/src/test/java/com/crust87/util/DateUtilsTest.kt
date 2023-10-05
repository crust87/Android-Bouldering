package com.crust87.util

import android.content.Context
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DateUtilsTest : BehaviorSpec({
    val mockContext = mockk<Context>()

    every { mockContext.getString(R.string.time_just_now) } returns "지금 막"
    every { mockContext.getString(R.string.time_min) } returns "%d분 전"
    every { mockContext.getString(R.string.time_hrs) } returns "%d시간 전"
    every { mockContext.getString(R.string.time_day) } returns "%d일 전"
    every { mockContext.getString(R.string.time_date) } returns "yy.MM.dd"
    every { mockContext.getString(R.string.time_date_time) } returns "yyyy. MM. dd h:mma"

    DateUtils.initialize(mockContext)

    given("현재 시간 기준 으로") {
        val current = System.currentTimeMillis()

        `when`("지나간 시간이") {
            then("0면 지금 막 출력") {
                DateUtils.convertDate(current, current) shouldBe "지금 막"
            }

            then("60초면 지금 막 출력") {
                DateUtils.convertDate(current - 60000, current) shouldBe "지금 막"
            }

            then("61초면 1분 전 출력") {
                DateUtils.convertDate(current - 61000, current) shouldBe "1분 전"
            }

            then("3599초면 59분 전 출력") {
                DateUtils.convertDate(current - 3599000, current) shouldBe "59분 전"
            }

            then("3600초면 60분 전 출력") {
                DateUtils.convertDate(current - 3600000, current) shouldBe "60분 전"
            }

            then("3601초면 1시간 전 출력") {
                DateUtils.convertDate(current - 3601000, current) shouldBe "1시간 전"
            }

            then("86400초면 24시간 전 출력") {
                DateUtils.convertDate(current - 86400000, current) shouldBe "24시간 전"
            }

            then("86401초면 1일 전 출력") {
                DateUtils.convertDate(current - 86401000, current) shouldBe "1일 전"
            }

            then("2419200초면 지금 28일 전 출력") {
                DateUtils.convertDate(current - 2419200000, current) shouldBe "28일 전"
            }
        }
    }

    given("기준일 2023년 1월 1일 0시 기준") {
        val current = 1672498800000L

        `when`("convertDateTime 이용 날짜 변환 하면") {
            then("2023. 01. 01 12:00AM 출력") {
                DateUtils.convertDateTime(current) shouldBe "2023. 01. 01 12:00AM"
            }
        }

        `when`("지나간 시간이") {
            then("2419201초면 22.12.03 출력") {
                DateUtils.convertDate(current - 2419201000, current) shouldBe "22.12.03"
            }
        }
    }
})